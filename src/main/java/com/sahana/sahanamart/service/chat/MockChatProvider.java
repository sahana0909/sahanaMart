package com.sahana.sahanamart.service.chat;

import java.util.Locale;

/**
 * Intelligent Rule-Based Mock Chat Provider with Predefined FAQ Answers.
 * Operates standalone with zero external API dependencies.
 */
public class MockChatProvider implements ChatProvider {

    @Override
    public String getProviderName() {
        return "SahanaMart Smart FAQ Assistant";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String generateReply(String userMessage, String sessionId) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return "Hello! How may I assist your SahanaMart shopping today?";
        }

        String msg = userMessage.trim().toLowerCase(Locale.ROOT);

        // Greetings
        if (msg.matches(".*\\b(hello|hi|hey|good morning|good evening|good afternoon|namaste|vanakkam)\\b.*")) {
            return "Hello! Welcome to SahanaMart. I can assist you with tracking orders, payment queries, product recommendations, delivery policies, or becoming a seller. What would you like help with?";
        }

        // Order Tracking & Status
        if (msg.contains("track") || msg.contains("order status") || msg.contains("where is my order")
                || msg.contains("check order") || msg.contains("order history")) {
            return "You can view and track your active orders by navigating to 'My Orders' in the top menu. Orders progress through four transparent stages: PENDING -> CONFIRMED -> SHIPPED -> DELIVERED.";
        }

        // Payment Methods & Mock Simulation
        if (msg.contains("payment") || msg.contains("pay") || msg.contains("upi") || msg.contains("card")
                || msg.contains("credit") || msg.contains("debit") || msg.contains("cash on delivery") || msg.contains("cod")) {
            return "SahanaMart supports three payment options during checkout: Simulated Instant UPI (Google Pay / PhonePe), Simulated Credit/Debit Card (3D Secure), and Cash on Delivery (COD). All transactions are processed atomically with server-side inventory verification.";
        }

        // Shipping & Delivery
        if (msg.contains("shipping") || msg.contains("delivery") || msg.contains("courier")
                || msg.contains("dispatch") || msg.contains("how long") || msg.contains("arrive")) {
            return "Standard delivery takes 2 to 4 business days across Tamil Nadu and metro hubs. Standard shipping is complimentary on all orders, and your delivery destination can be specified during checkout.";
        }

        // Returns & Refunds & Cancellations
        if (msg.contains("return") || msg.contains("refund") || msg.contains("exchange")
                || msg.contains("cancel") || msg.contains("cancellation")) {
            return "SahanaMart offers a 7-day hassle-free return and refund policy on eligible delivered items. If your package arrives damaged or defective, contact support or request assistance from your order summary for an immediate refund.";
        }

        // Seller Onboarding & Selling
        if (msg.contains("seller") || msg.contains("sell") || msg.contains("vendor")
                || msg.contains("merchant") || msg.contains("list product") || msg.contains("create listing")) {
            return "To sell on SahanaMart, choose 'Seller' during registration at /register. Once logged in, your Seller Dashboard gives you full control to publish listings, upload product photos, monitor inventory, and process incoming buyer orders.";
        }

        // Customer Reviews & Ratings
        if (msg.contains("review") || msg.contains("rating") || msg.contains("rate") || msg.contains("star") || msg.contains("feedback")) {
            return "Verified buyers can submit a 1 to 5-star rating and detailed written review on any delivered order! Look for the '★ Rate & Review' button on your completed order in the 'My Orders' page or on the product details page.";
        }

        // Products & Categories
        if (msg.contains("product") || msg.contains("category") || msg.contains("categories")
                || msg.contains("laptop") || msg.contains("electronics") || msg.contains("fashion")
                || msg.contains("book") || msg.contains("catalog") || msg.contains("items")) {
            return "We offer curated collections across Electronics (laptops, audio, accessories), Fashion & Apparel, Home & Living, and Academic Engineering Literature. You can filter by category pills or use the search bar above.";
        }

        // Security & Passwords
        if (msg.contains("password") || msg.contains("security") || msg.contains("safe")
                || msg.contains("bcrypt") || msg.contains("salt") || msg.contains("hack")) {
            return "Your account is secured using industry-standard jBCrypt hashing with 12 salt rounds, parameterized SQL queries preventing injection, and session fixation defense. Plaintext passwords are never logged or stored.";
        }

        // Tech Stack & Capstone Details
        if (msg.contains("tech stack") || msg.contains("technology") || msg.contains("anna university")
                || msg.contains("capstone") || msg.contains("who built") || msg.contains("architecture")) {
            return "SahanaMart is developed by Sahana D for Anna University R2025 Semester 3 CSE. It is architected strictly using Java 17 Servlets, JSP/JSTL, HikariCP, H2/MySQL persistent storage, and Apache Tomcat 9, following layered MVC design.";
        }

        // Admin & Moderation
        if (msg.contains("admin") || msg.contains("moderate") || msg.contains("platform")) {
            return "Admin accounts are pre-seeded securely per capstone specifications. The Admin Panel allows platform administrators to audit user accounts, monitor global orders, and deactivate inappropriate listings.";
        }

        // Polite Fallback for unrelated questions
        return "I am SahanaMart's shopping assistant. I specialize in helping you with our products, categories, orders, shipping, returns, payment methods, reviews, and selling on SahanaMart. How may I assist your shopping experience today?";
    }
}
