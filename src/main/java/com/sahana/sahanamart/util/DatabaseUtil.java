package com.sahana.sahanamart.util;

import com.sahana.sahanamart.exception.DatabaseException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DatabaseUtil {

    private static final Logger LOGGER = Logger.getLogger(DatabaseUtil.class.getName());
    private static volatile HikariDataSource dataSource;

    private static final String DEFAULT_JDBC_URL =
        "jdbc:h2:file:~/.sahanamart/data/sahanamart_db;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE";
    private static final String DEFAULT_USER = "sa";
    private static final String DEFAULT_PASSWORD = "";

    private DatabaseUtil() {}

    public static synchronized void initDataSource() {
        initDataSource(DEFAULT_JDBC_URL, DEFAULT_USER, DEFAULT_PASSWORD);
    }

    private static void checkAndCleanStaleLock() {
        try {
            File lockFile = new File(System.getProperty("user.home"), ".sahanamart/data/sahanamart_db.lock.db");
            if (lockFile.exists()) {
                String serverAddress = null;
                try (BufferedReader reader = new BufferedReader(new FileReader(lockFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("server=")) {
                            serverAddress = line.substring("server=".length()).replace("\\:", ":").trim();
                            break;
                        }
                    }
                }
                boolean alive = false;
                if (serverAddress != null && serverAddress.contains(":")) {
                    String[] parts = serverAddress.split(":");
                    String host = parts[0];
                    int port = Integer.parseInt(parts[1]);
                    try (Socket testSocket = new Socket()) {
                        testSocket.connect(new InetSocketAddress(host, port), 250);
                        alive = true;
                    } catch (Exception ignored) {
                        alive = false;
                    }
                }
                if (!alive) {
                    LOGGER.info("Stale H2 lock file detected (server not reachable). Deleting: " + lockFile.getAbsolutePath());
                    lockFile.delete();
                }
            }
        } catch (Exception e) {
            LOGGER.warning("Could not check H2 lock file status: " + e.getMessage());
        }
    }

    public static synchronized void initDataSource(String jdbcUrl, String username, String password) {
        if (dataSource != null && !dataSource.isClosed()) {
            return;
        }

        checkAndCleanStaleLock();

        try {
            Class.forName("org.h2.Driver");
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(username != null ? username : DEFAULT_USER);
            config.setPassword(password != null ? password : DEFAULT_PASSWORD);
            config.setMaximumPoolSize(15);
            config.setMinimumIdle(3);
            config.setIdleTimeout(30000);
            config.setMaxLifetime(1800000);
            config.setConnectionTimeout(10000);
            config.setPoolName("SahanaMartHikariPool");

            dataSource = new HikariDataSource(config);
            LOGGER.info("HikariCP connection pool initialized successfully with URL: " + jdbcUrl);
        } catch (Exception e) {
            if (jdbcUrl != null && jdbcUrl.contains("AUTO_SERVER=TRUE")) {
                LOGGER.warning("Initial pool init with AUTO_SERVER=TRUE failed. Attempting recovery...");
                try {
                    File lockFile = new File(System.getProperty("user.home"), ".sahanamart/data/sahanamart_db.lock.db");
                    if (lockFile.exists()) {
                        LOGGER.info("Removing stale H2 lock file: " + lockFile.getAbsolutePath());
                        lockFile.delete();
                    }
                    String fallbackUrl = jdbcUrl.replace(";AUTO_SERVER=TRUE", "");
                    HikariConfig fallbackConfig = new HikariConfig();
                    fallbackConfig.setJdbcUrl(fallbackUrl);
                    fallbackConfig.setUsername(username != null ? username : DEFAULT_USER);
                    fallbackConfig.setPassword(password != null ? password : DEFAULT_PASSWORD);
                    fallbackConfig.setMaximumPoolSize(15);
                    fallbackConfig.setMinimumIdle(3);
                    fallbackConfig.setIdleTimeout(30000);
                    fallbackConfig.setMaxLifetime(1800000);
                    fallbackConfig.setConnectionTimeout(10000);
                    fallbackConfig.setPoolName("SahanaMartHikariPoolFallback");

                    dataSource = new HikariDataSource(fallbackConfig);
                    LOGGER.info("HikariCP recovered successfully using direct file URL: " + fallbackUrl);
                    return;
                } catch (Exception fallbackEx) {
                    LOGGER.log(Level.SEVERE, "Recovery failed", fallbackEx);
                }
            }
            LOGGER.log(Level.SEVERE, "Failed to initialize HikariCP connection pool", e);
            throw new DatabaseException("Failed to initialize database connection pool", e);
        }
    }

    public static synchronized void setDataSource(HikariDataSource ds) {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
        dataSource = ds;
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            initDataSource();
        }
        return dataSource.getConnection();
    }

    public static boolean isHealthy() {
        if (dataSource == null || dataSource.isClosed()) {
            return false;
        }
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            return stmt.execute("SELECT 1");
        } catch (Exception e) {
            return false;
        }
    }

    public static void runScript(Connection conn, String classpathResource) {
        LOGGER.info("Executing database script: " + classpathResource);
        try (InputStream is = DatabaseUtil.class.getClassLoader().getResourceAsStream(classpathResource)) {
            if (is == null) {
                LOGGER.warning("Script not found on classpath: " + classpathResource);
                return;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                 Statement stmt = conn.createStatement()) {

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    String trimmed = line.trim();
                    if (trimmed.startsWith("--") || trimmed.isEmpty()) {
                        continue;
                    }
                    sb.append(line).append("\n");
                    if (trimmed.endsWith(";")) {
                        String sql = sb.toString().trim();
                        sql = sql.substring(0, sql.length() - 1).trim();
                        if (!sql.isEmpty()) {
                            stmt.execute(sql);
                        }
                        sb.setLength(0);
                    }
                }
                if (sb.length() > 0 && !sb.toString().trim().isEmpty()) {
                    stmt.execute(sb.toString().trim());
                }
            }
            LOGGER.info("Successfully executed script: " + classpathResource);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error executing script: " + classpathResource, e);
            throw new DatabaseException("Error executing script " + classpathResource, e);
        }
    }

    public static synchronized void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            LOGGER.info("Shutting down HikariCP connection pool...");
            dataSource.close();
            dataSource = null;
        }
    }
}
