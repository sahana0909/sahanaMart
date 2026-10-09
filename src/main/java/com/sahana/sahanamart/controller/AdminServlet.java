package com.sahana.sahanamart.controller;

import com.sahana.sahanamart.dto.OrderDTO;
import com.sahana.sahanamart.dto.ProductDTO;
import com.sahana.sahanamart.dto.UserResponseDTO;
import com.sahana.sahanamart.model.User;
import com.sahana.sahanamart.service.OrderService;
import com.sahana.sahanamart.service.ProductService;
import com.sahana.sahanamart.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminServlet", urlPatterns = {"/admin/dashboard", "/admin/listings/moderate", "/admin/products/toggle-status"})
public class AdminServlet extends HttpServlet {

    private UserService getUserService() {
        return (UserService) getServletContext().getAttribute("userService");
    }

    private ProductService getProductService() {
        return (ProductService) getServletContext().getAttribute("productService");
    }

    private OrderService getOrderService() {
        return (OrderService) getServletContext().getAttribute("orderService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        List<UserResponseDTO> users = getUserService().getAllUsers();
        List<OrderDTO> orders = getOrderService().getAllOrders();
        List<ProductDTO> products = getProductService().searchAndFilter(null, null, false, 1, 50);

        req.setAttribute("users", users);
        req.setAttribute("orders", orders);
        req.setAttribute("products", products);

        req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/admin/listings/moderate".equals(path) || "/admin/products/toggle-status".equals(path)) {
            String productIdStr = req.getParameter("productId");
            String activeStr = req.getParameter("active");
            if (productIdStr != null && activeStr != null) {
                try {
                    Long productId = Long.parseLong(productIdStr.trim());
                    boolean active = Boolean.parseBoolean(activeStr.trim());
                    getProductService().setListingStatus(productId, active);
                    req.getSession().setAttribute("adminSuccess", "Listing #" + productId + " status set to " + (active ? "ACTIVE" : "INACTIVE"));
                } catch (Exception e) {
                    req.getSession().setAttribute("adminError", "Failed to moderate listing: " + e.getMessage());
                }
            }
        }
        resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
    }
}
