package com.sahana.sahanamart.controller.api;

import com.sahana.sahanamart.dto.ApiResponse;
import com.sahana.sahanamart.dto.ProductDTO;
import com.sahana.sahanamart.service.ProductService;
import com.sahana.sahanamart.util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "ProductApiServlet", urlPatterns = {"/api/v1/products", "/api/v1/products/*"})
public class ProductApiServlet extends HttpServlet {

    private ProductService getProductService() {
        return (ProductService) getServletContext().getAttribute("productService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String pathInfo = req.getPathInfo();

        if (pathInfo != null && pathInfo.length() > 1) {
            // /api/v1/products/{id}
            try {
                Long productId = Long.parseLong(pathInfo.substring(1));
                var productOpt = getProductService().getProductById(productId);
                if (productOpt.isPresent()) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    JsonUtil.writeJson(resp.getWriter(), ApiResponse.ok(productOpt.get()));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    JsonUtil.writeJson(resp.getWriter(), ApiResponse.error("Product not found"));
                }
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonUtil.writeJson(resp.getWriter(), ApiResponse.error("Invalid product ID"));
            }
            return;
        }

        // /api/v1/products list
        String keyword = req.getParameter("keyword");
        String categoryIdStr = req.getParameter("categoryId");
        Long categoryId = null;
        if (categoryIdStr != null && !categoryIdStr.trim().isEmpty()) {
            try {
                categoryId = Long.parseLong(categoryIdStr.trim());
            } catch (NumberFormatException ignored) {}
        }

        List<ProductDTO> products = getProductService().searchAndFilter(keyword, categoryId, true, 1, 50);
        resp.setStatus(HttpServletResponse.SC_OK);
        JsonUtil.writeJson(resp.getWriter(), ApiResponse.ok(products));
    }
}
