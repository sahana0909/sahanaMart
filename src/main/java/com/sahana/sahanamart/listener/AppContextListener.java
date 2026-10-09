package com.sahana.sahanamart.listener;

import com.sahana.sahanamart.dao.CartDAO;
import com.sahana.sahanamart.dao.OrderDAO;
import com.sahana.sahanamart.dao.ProductDAO;
import com.sahana.sahanamart.dao.ReviewDAO;
import com.sahana.sahanamart.dao.UserDAO;
import com.sahana.sahanamart.dao.impl.CartDAOImpl;
import com.sahana.sahanamart.dao.impl.OrderDAOImpl;
import com.sahana.sahanamart.dao.impl.ProductDAOImpl;
import com.sahana.sahanamart.dao.impl.ReviewDAOImpl;
import com.sahana.sahanamart.dao.impl.UserDAOImpl;
import com.sahana.sahanamart.model.Role;
import com.sahana.sahanamart.model.User;
import com.sahana.sahanamart.service.CartService;
import com.sahana.sahanamart.service.CartServiceImpl;
import com.sahana.sahanamart.service.OrderService;
import com.sahana.sahanamart.service.OrderServiceImpl;
import com.sahana.sahanamart.service.ProductService;
import com.sahana.sahanamart.service.ProductServiceImpl;
import com.sahana.sahanamart.service.ReviewService;
import com.sahana.sahanamart.service.ReviewServiceImpl;
import com.sahana.sahanamart.service.UserService;
import com.sahana.sahanamart.service.UserServiceImpl;
import com.sahana.sahanamart.util.DatabaseUtil;
import com.sahana.sahanamart.util.PasswordUtil;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(AppContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOGGER.info("Initializing SahanaMart application context...");
        ServletContext context = sce.getServletContext();

        try {
            // 1. Initialize Connection Pool
            DatabaseUtil.initDataSource();

            // 2. Run Database Schema & Migrations
            try (Connection conn = DatabaseUtil.getConnection()) {
                DatabaseUtil.runScript(conn, "db/schema.sql");
                DatabaseUtil.runScript(conn, "db/migrations/V2__order_status_workflow.sql");
            }

            // 3. Initialize DAOs
            UserDAO userDAO = new UserDAOImpl();
            ProductDAO productDAO = new ProductDAOImpl();
            CartDAO cartDAO = new CartDAOImpl();
            OrderDAO orderDAO = new OrderDAOImpl();
            ReviewDAO reviewDAO = new ReviewDAOImpl();

            // Check if seed data needs to be populated
            if (userDAO.count() == 0) {
                LOGGER.info("Database is empty. Seeding initial accounts and catalog...");

                // Seed Admin
                User admin = new User(null, "admin@sahanamart.com", PasswordUtil.hash("Admin@123"), "System Administrator", Role.ADMIN, "+91 9876543210", null);
                userDAO.create(admin);

                // Seed Seller
                User seller = new User(null, "seller@sahanamart.com", PasswordUtil.hash("Seller@123"), "Ananya Tech Supplies", Role.SELLER, "+91 9876543211", null);
                userDAO.create(seller);

                // Seed Buyer
                User buyer = new User(null, "buyer@sahanamart.com", PasswordUtil.hash("Buyer@123"), "Sahana User", Role.BUYER, "+91 9876543212", null);
                userDAO.create(buyer);

                // Execute catalog seed script (categories & products)
                try (Connection conn = DatabaseUtil.getConnection()) {
                    DatabaseUtil.runScript(conn, "db/seed.sql");
                }
            }

            // 4. Initialize Services
            UserService userService = new UserServiceImpl(userDAO);
            ProductService productService = new ProductServiceImpl(productDAO);
            CartService cartService = new CartServiceImpl(cartDAO, productDAO);
            OrderService orderService = new OrderServiceImpl(orderDAO, cartDAO, productDAO);
            ReviewService reviewService = new ReviewServiceImpl(reviewDAO, orderDAO);
            com.sahana.sahanamart.service.chat.ChatService chatService = new com.sahana.sahanamart.service.chat.ChatService();

            // 5. Store in ServletContext for Servlets to retrieve
            context.setAttribute("userService", userService);
            context.setAttribute("productService", productService);
            context.setAttribute("cartService", cartService);
            context.setAttribute("orderService", orderService);
            context.setAttribute("reviewService", reviewService);
            context.setAttribute("chatService", chatService);

            context.setAttribute("categories", productService.getAllCategories());

            LOGGER.info("SahanaMart application context initialized successfully.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize SahanaMart application context", e);
            throw new RuntimeException("Application initialization failed", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("Destroying SahanaMart application context...");
        DatabaseUtil.shutdown();
        LOGGER.info("SahanaMart application context destroyed.");
    }
}
