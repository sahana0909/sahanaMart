package com.sahana.sahanamart.util;

import com.sahana.sahanamart.exception.ValidationException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

    private ValidationUtil() {}

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static void validateRegistration(String email, String password, String confirmPassword, String fullName, String role) {
        Map<String, String> errors = new HashMap<>();

        if (email == null || email.trim().isEmpty()) {
            errors.put("email", "Email is required");
        } else if (!isValidEmail(email)) {
            errors.put("email", "Please enter a valid email address");
        }

        if (password == null || password.length() < 6) {
            errors.put("password", "Password must be at least 6 characters");
        }

        if (confirmPassword == null || !confirmPassword.equals(password)) {
            errors.put("confirmPassword", "Passwords do not match");
        }

        if (fullName == null || fullName.trim().isEmpty()) {
            errors.put("fullName", "Full name is required");
        } else if (fullName.trim().length() > 100) {
            errors.put("fullName", "Full name cannot exceed 100 characters");
        }

        if (role == null || (!role.equalsIgnoreCase("BUYER") && !role.equalsIgnoreCase("SELLER"))) {
            errors.put("role", "Role must be either BUYER or SELLER");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Registration validation failed", errors);
        }
    }

    public static void validateProduct(String name, String description, BigDecimal price, Integer stockQty, Long categoryId) {
        Map<String, String> errors = new HashMap<>();

        if (name == null || name.trim().isEmpty()) {
            errors.put("name", "Product name is required");
        } else if (name.trim().length() > 200) {
            errors.put("name", "Product name cannot exceed 200 characters");
        }

        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            errors.put("price", "Price must be greater than 0");
        }

        if (stockQty == null || stockQty < 0) {
            errors.put("stockQty", "Stock quantity cannot be negative");
        }

        if (categoryId == null || categoryId <= 0) {
            errors.put("categoryId", "A valid category must be selected");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Product validation failed", errors);
        }
    }

    public static void validateReview(int rating, String comment) {
        Map<String, String> errors = new HashMap<>();

        if (rating < 1 || rating > 5) {
            errors.put("rating", "Rating must be between 1 and 5 stars");
        }

        if (comment != null && comment.trim().length() > 2000) {
            errors.put("comment", "Review comment cannot exceed 2000 characters");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Review validation failed", errors);
        }
    }
}
