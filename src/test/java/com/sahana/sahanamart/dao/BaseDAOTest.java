package com.sahana.sahanamart.dao;

import com.sahana.sahanamart.util.DatabaseUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.sql.Connection;

public abstract class BaseDAOTest {

    @BeforeAll
    public static void setUpDatabase() throws Exception {
        // Use in-memory H2 database for testing
        DatabaseUtil.initDataSource("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=LEGACY", "sa", "");
        try (Connection conn = DatabaseUtil.getConnection()) {
            DatabaseUtil.runScript(conn, "test-schema.sql");
        }
    }

    @AfterAll
    public static void tearDownDatabase() {
        DatabaseUtil.shutdown();
    }
}
