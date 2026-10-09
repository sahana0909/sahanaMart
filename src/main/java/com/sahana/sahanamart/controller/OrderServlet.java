package com.sahana.sahanamart.controller;

import com.sahana.sahanamart.dto.OrderDTO;
import com.sahana.sahanamart.exception.ValidationException;
import com.sahana.sahanamart.model.OrderStatus;
import com.sahana.sahanamart.model.User;
import com.sahana.sahanamart.service.OrderService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "OrderServlet", urlPatterns = {"/buyer/orders", "/seller/orders", "/seller/orders/status", "/seller/orders/update-status"})
public class OrderServlet extends HttpServlet {

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
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String path = req.getServletPath();

        if ("/seller/orders".equals(path)) {
            List<OrderDTO> orders = getOrderService().getSellerIncomingOrders(user.getId());
            req.setAttribute("orders", orders);
            req.getRequestDispatcher("/WEB-INF/views/seller/orders.jsp").forward(req, resp);
        } else {
            // /buyer/orders
            List<OrderDTO> orders = getOrderService().getBuyerOrders(user.getId());
            req.setAttribute("orders", orders);
            req.getRequestDispatcher("/WEB-INF/views/buyer/orders.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getCurrentUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String orderIdStr = req.getParameter("orderId");
        String nextStatusStr = req.getParameter("status");

        try {
            Long orderId = Long.parseLong(orderIdStr.trim());
            OrderStatus newStatus = OrderStatus.fromString(nextStatusStr);
            getOrderService().updateOrderStatus(orderId, newStatus, user.getId(), user.getRole());
            req.getSession().setAttribute("orderSuccess", "Order #" + orderId + " status updated to " + newStatus);
        } catch (ValidationException e) {
            req.getSession().setAttribute("orderError", e.getMessage());
        } catch (Exception e) {
            req.getSession().setAttribute("orderError", "Failed to update order status: " + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/seller/orders");
    }
}
