package com.sahana.sahanamart.controller;

import com.sahana.sahanamart.dto.ProductDTO;
import com.sahana.sahanamart.exception.ValidationException;
import com.sahana.sahanamart.model.User;
import com.sahana.sahanamart.service.ProductService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet(name = "SellerServlet", urlPatterns = {
    "/seller/dashboard",
    "/seller/product/create",
    "/seller/product/edit",
    "/seller/product/delete"
})
public class SellerServlet extends HttpServlet {

    private ProductService getProductService() {
        return (ProductService) getServletContext().getAttribute("productService");
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

        List<ProductDTO> products = getProductService().getSellerProducts(user.getId());
        req.setAttribute("products", products);
        req.setAttribute("categories", getProductService().getAllCategories());

        req.getRequestDispatcher("/WEB-INF/views/seller/dashboard.jsp").forward(req, resp);
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
                case "/seller/product/create":
                    handleCreate(req, resp, user.getId());
                    break;
                case "/seller/product/edit":
                    handleEdit(req, resp, user.getId());
                    break;
                case "/seller/product/delete":
                    handleDelete(req, resp, user.getId());
                    break;
                default:
                    resp.sendRedirect(req.getContextPath() + "/seller/dashboard");
            }
        } catch (ValidationException e) {
            req.getSession().setAttribute("sellerError", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/seller/dashboard");
        } catch (Exception e) {
            req.getSession().setAttribute("sellerError", "Operation failed: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/seller/dashboard");
        }
    }

    private void handleCreate(HttpServletRequest req, HttpServletResponse resp, Long sellerId) throws IOException {
        String name = req.getParameter("name");
        String description = req.getParameter("description");
        String priceStr = req.getParameter("price");
        String stockStr = req.getParameter("stockQty");
        String categoryIdStr = req.getParameter("categoryId");
        String imageUrl = req.getParameter("imageUrl");

        BigDecimal price = new BigDecimal(priceStr.trim());
        int stock = Integer.parseInt(stockStr.trim());
        Long categoryId = Long.parseLong(categoryIdStr.trim());

        getProductService().createProduct(sellerId, name, description, price, stock, categoryId, imageUrl);
        req.getSession().setAttribute("sellerSuccess", "Product listing created successfully!");
        resp.sendRedirect(req.getContextPath() + "/seller/dashboard");
    }

    private void handleEdit(HttpServletRequest req, HttpServletResponse resp, Long sellerId) throws IOException {
        String productIdStr = req.getParameter("productId");
        String name = req.getParameter("name");
        String description = req.getParameter("description");
        String priceStr = req.getParameter("price");
        String stockStr = req.getParameter("stockQty");
        String categoryIdStr = req.getParameter("categoryId");
        String imageUrl = req.getParameter("imageUrl");
        boolean active = "on".equalsIgnoreCase(req.getParameter("active")) || "true".equalsIgnoreCase(req.getParameter("active"));

        Long productId = Long.parseLong(productIdStr.trim());
        BigDecimal price = new BigDecimal(priceStr.trim());
        int stock = Integer.parseInt(stockStr.trim());
        Long categoryId = Long.parseLong(categoryIdStr.trim());

        getProductService().updateProduct(sellerId, productId, name, description, price, stock, categoryId, imageUrl, active);
        req.getSession().setAttribute("sellerSuccess", "Product listing updated successfully!");
        resp.sendRedirect(req.getContextPath() + "/seller/dashboard");
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp, Long sellerId) throws IOException {
        String productIdStr = req.getParameter("productId");
        Long productId = Long.parseLong(productIdStr.trim());
        getProductService().deleteProduct(sellerId, productId);
        req.getSession().setAttribute("sellerSuccess", "Product listing deleted successfully!");
        resp.sendRedirect(req.getContextPath() + "/seller/dashboard");
    }
}
