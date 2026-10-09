package com.sahana.sahanamart.controller;

import com.sahana.sahanamart.dto.ProductDTO;
import com.sahana.sahanamart.dto.ReviewDTO;
import com.sahana.sahanamart.model.User;
import com.sahana.sahanamart.service.ProductService;
import com.sahana.sahanamart.service.ReviewService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "BuyerServlet", urlPatterns = {"/products", "/product", "/search"})
public class BuyerServlet extends HttpServlet {

    private static final int PAGE_SIZE = 12;

    private ProductService getProductService() {
        return (ProductService) getServletContext().getAttribute("productService");
    }

    private ReviewService getReviewService() {
        return (ReviewService) getServletContext().getAttribute("reviewService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/product".equals(path)) {
            handleProductDetail(req, resp);
        } else {
            handleBrowseProducts(req, resp);
        }
    }

    private void handleBrowseProducts(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String keyword = req.getParameter("keyword");
        String categoryIdStr = req.getParameter("categoryId");
        String pageStr = req.getParameter("page");

        Long categoryId = null;
        if (categoryIdStr != null && !categoryIdStr.trim().isEmpty()) {
            try {
                categoryId = Long.parseLong(categoryIdStr.trim());
            } catch (NumberFormatException ignored) {}
        }

        int page = 1;
        if (pageStr != null && !pageStr.trim().isEmpty()) {
            try {
                page = Math.max(1, Integer.parseInt(pageStr.trim()));
            } catch (NumberFormatException ignored) {}
        }

        List<ProductDTO> products = getProductService().searchAndFilter(keyword, categoryId, true, page, PAGE_SIZE);
        int totalCount = getProductService().getTotalCount(keyword, categoryId, true);
        int totalPages = (int) Math.ceil((double) totalCount / PAGE_SIZE);

        req.setAttribute("products", products);
        req.setAttribute("totalCount", totalCount);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", Math.max(1, totalPages));
        req.setAttribute("keyword", keyword);
        req.setAttribute("selectedCategoryId", categoryId);
        req.setAttribute("categories", getProductService().getAllCategories());

        req.getRequestDispatcher("/WEB-INF/views/buyer/products.jsp").forward(req, resp);
    }

    private void handleProductDetail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/products");
            return;
        }

        try {
            Long productId = Long.parseLong(idStr.trim());
            var productOpt = getProductService().getProductById(productId);
            if (productOpt.isEmpty() || !Boolean.TRUE.equals(productOpt.get().getActive())) {
                req.setAttribute("errorMessage", "Product not found or is currently inactive.");
                req.getRequestDispatcher("/WEB-INF/views/error/404.jsp").forward(req, resp);
                return;
            }

            ProductDTO product = productOpt.get();
            List<ReviewDTO> reviews = getReviewService().getProductReviews(productId);
            double avgRating = getReviewService().getAverageRating(productId);
            int reviewCount = getReviewService().getReviewCount(productId);

            User currentUser = (User) req.getSession().getAttribute("currentUser");
            boolean canReview = false;
            if (currentUser != null) {
                var eligibleOpt = getReviewService().getEligibleOrderIdForReview(currentUser.getId(), productId);
                if (eligibleOpt.isPresent()) {
                    canReview = true;
                    req.setAttribute("eligibleOrderId", eligibleOpt.get());
                }
            }

            req.setAttribute("product", product);
            req.setAttribute("reviews", reviews);
            req.setAttribute("averageRating", avgRating);
            req.setAttribute("reviewCount", reviewCount);
            req.setAttribute("canReview", canReview);

            req.getRequestDispatcher("/WEB-INF/views/buyer/product-detail.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/products");
        }
    }
}
