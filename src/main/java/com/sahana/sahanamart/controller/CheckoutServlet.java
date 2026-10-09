package com.sahana.sahanamart.controller;

import com.sahana.sahanamart.dto.CartItemDTO;
import com.sahana.sahanamart.dto.OrderDTO;
import com.sahana.sahanamart.exception.ValidationException;
import com.sahana.sahanamart.model.User;
import com.sahana.sahanamart.service.CartService;
import com.sahana.sahanamart.service.OrderService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@WebServlet(name = "CheckoutServlet", urlPatterns = {"/checkout", "/checkout/place-order", "/order-confirmation"})
public class CheckoutServlet extends HttpServlet {

    private CartService getCartService() {
        return (CartService) getServletContext().getAttribute("cartService");
    }

    private OrderService getOrderService() {
        return (OrderService) getServletContext().getAttribute("orderService");
    }

    private User getCurrentUser(HttpServletRequest req) {
        return (User) req.getSession().getAttribute("currentUser");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getCurrentUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login?returnUrl=" + req.getRequestURI());
            return;
        }

        String path = req.getServletPath();

        if ("/order-confirmation".equals(path)) {
            String orderNumber = req.getParameter("orderNumber");
            if (orderNumber != null && !orderNumber.trim().isEmpty()) {
                var orderOpt = getOrderService().getOrderByOrderNumber(orderNumber.trim());
                if (orderOpt.isPresent()) {
                    OrderDTO order = orderOpt.get();
                    // Prevent IDOR: Verify order belongs to current user or user is ADMIN
                    if (order.getBuyerId().equals(user.getId()) || user.getRole() == com.sahana.sahanamart.model.Role.ADMIN) {
                        req.setAttribute("order", order);
                        req.getRequestDispatcher("/WEB-INF/views/buyer/order-confirmation.jsp").forward(req, resp);
                        return;
                    } else {
                        resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied: You are not authorized to view this order confirmation");
                        return;
                    }
                }
            }
            resp.sendRedirect(req.getContextPath() + "/buyer/orders");
            return;
        }

        // /checkout GET
        List<CartItemDTO> cartItems = getCartService().getCart(user.getId());
        if (cartItems.isEmpty()) {
            req.getSession().setAttribute("cartError", "Your cart is empty. Please add items before checking out.");
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        BigDecimal subtotal = getCartService().calculateSubtotal(user.getId());
        BigDecimal tax = subtotal.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(tax);

        req.setAttribute("cartItems", cartItems);
        req.setAttribute("subtotal", subtotal);
        req.setAttribute("tax", tax);
        req.setAttribute("total", total);

        req.getRequestDispatcher("/WEB-INF/views/buyer/checkout.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getCurrentUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String shippingAddress = req.getParameter("shippingAddress");
        String paymentMethod = req.getParameter("paymentMethod");

        try {
            OrderDTO placedOrder = getOrderService().checkout(user.getId(), shippingAddress, paymentMethod);
            // Reset cart count in session
            req.getSession().setAttribute("cartCount", 0);
            resp.sendRedirect(req.getContextPath() + "/order-confirmation?orderNumber=" + placedOrder.getOrderNumber());
        } catch (ValidationException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("shippingAddress", shippingAddress);
            List<CartItemDTO> cartItems = getCartService().getCart(user.getId());
            BigDecimal subtotal = getCartService().calculateSubtotal(user.getId());
            BigDecimal tax = subtotal.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP);
            req.setAttribute("cartItems", cartItems);
            req.setAttribute("subtotal", subtotal);
            req.setAttribute("tax", tax);
            req.setAttribute("total", subtotal.add(tax));
            req.getRequestDispatcher("/WEB-INF/views/buyer/checkout.jsp").forward(req, resp);
        }
    }
}
