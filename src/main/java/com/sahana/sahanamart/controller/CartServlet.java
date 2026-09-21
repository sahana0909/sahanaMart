package com.sahana.sahanamart.controller;

import com.sahana.sahanamart.dto.CartItemDTO;
import com.sahana.sahanamart.exception.ValidationException;
import com.sahana.sahanamart.model.User;
import com.sahana.sahanamart.service.CartService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@WebServlet(name = "CartServlet", urlPatterns = {"/cart", "/cart/add", "/cart/update", "/cart/remove", "/cart/clear"})
public class CartServlet extends HttpServlet {

    private CartService getCartService() {
        return (CartService) getServletContext().getAttribute("cartService");
    }

    private User getCurrentUser(HttpServletRequest req) {
        return (User) req.getSession().getAttribute("currentUser");
    }

    private void updateSessionCartCount(HttpServletRequest req, Long userId) {
        int count = getCartService().getCartItemCount(userId);
        req.getSession().setAttribute("cartCount", count);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getCurrentUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login?returnUrl=" + req.getRequestURI());
            return;
        }

        List<CartItemDTO> cartItems = getCartService().getCart(user.getId());
        BigDecimal subtotal = getCartService().calculateSubtotal(user.getId());
        BigDecimal tax = subtotal.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP); // 5% GST/tax
        BigDecimal total = subtotal.add(tax);

        req.setAttribute("cartItems", cartItems);
        req.setAttribute("subtotal", subtotal);
        req.setAttribute("tax", tax);
        req.setAttribute("total", total);

        updateSessionCartCount(req, user.getId());

        req.getRequestDispatcher("/WEB-INF/views/buyer/cart.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getCurrentUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String path = req.getServletPath();

        try {
            switch (path) {
                case "/cart/add":
                    handleAdd(req, resp, user.getId());
                    break;
                case "/cart/update":
                    handleUpdate(req, resp, user.getId());
                    break;
                case "/cart/remove":
                    handleRemove(req, resp, user.getId());
                    break;
                case "/cart/clear":
                    handleClear(req, resp, user.getId());
                    break;
                default:
                    resp.sendRedirect(req.getContextPath() + "/cart");
            }
        } catch (ValidationException e) {
            req.getSession().setAttribute("cartError", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/cart");
        }
    }

    private void handleAdd(HttpServletRequest req, HttpServletResponse resp, Long userId) throws IOException {
        String productIdStr = req.getParameter("productId");
        String quantityStr = req.getParameter("quantity");

        int quantity = 1;
        if (quantityStr != null && !quantityStr.trim().isEmpty()) {
            try {
                quantity = Math.max(1, Integer.parseInt(quantityStr.trim()));
            } catch (NumberFormatException ignored) {}
        }

        if (productIdStr != null) {
            try {
                Long productId = Long.parseLong(productIdStr.trim());
                getCartService().addToCart(userId, productId, quantity);
                updateSessionCartCount(req, userId);
                req.getSession().setAttribute("cartSuccess", "Item added to cart successfully!");
            } catch (ValidationException e) {
                req.getSession().setAttribute("cartError", e.getMessage());
            } catch (NumberFormatException ignored) {}
        }

        String referer = req.getHeader("Referer");
        if (referer != null && referer.contains("/product")) {
            resp.sendRedirect(referer);
        } else {
            resp.sendRedirect(req.getContextPath() + "/cart");
        }
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp, Long userId) throws IOException {
        String cartItemIdStr = req.getParameter("cartItemId");
        String quantityStr = req.getParameter("quantity");

        if (cartItemIdStr != null && quantityStr != null) {
            try {
                Long cartItemId = Long.parseLong(cartItemIdStr.trim());
                int quantity = Integer.parseInt(quantityStr.trim());
                getCartService().updateQuantity(userId, cartItemId, quantity);
                updateSessionCartCount(req, userId);
            } catch (NumberFormatException ignored) {}
        }
        resp.sendRedirect(req.getContextPath() + "/cart");
    }

    private void handleRemove(HttpServletRequest req, HttpServletResponse resp, Long userId) throws IOException {
        String cartItemIdStr = req.getParameter("cartItemId");
        if (cartItemIdStr != null) {
            try {
                Long cartItemId = Long.parseLong(cartItemIdStr.trim());
                getCartService().removeFromCart(userId, cartItemId);
                updateSessionCartCount(req, userId);
                req.getSession().setAttribute("cartSuccess", "Item removed from cart");
            } catch (NumberFormatException ignored) {}
        }
        resp.sendRedirect(req.getContextPath() + "/cart");
    }

    private void handleClear(HttpServletRequest req, HttpServletResponse resp, Long userId) throws IOException {
        getCartService().clearCart(userId);
        updateSessionCartCount(req, userId);
        resp.sendRedirect(req.getContextPath() + "/cart");
    }
}
