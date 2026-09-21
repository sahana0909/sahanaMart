package com.sahana.sahanamart.controller;

import com.sahana.sahanamart.exception.ValidationException;
import com.sahana.sahanamart.model.User;
import com.sahana.sahanamart.service.ReviewService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ReviewServlet", urlPatterns = {"/reviews/add"})
public class ReviewServlet extends HttpServlet {

    private ReviewService getReviewService() {
        return (ReviewService) getServletContext().getAttribute("reviewService");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String productIdStr = req.getParameter("productId");
        String orderIdStr = req.getParameter("orderId");
        String ratingStr = req.getParameter("rating");
        String comment = req.getParameter("comment");

        try {
            Long productId = Long.parseLong(productIdStr.trim());
            Long orderId = Long.parseLong(orderIdStr.trim());
            int rating = Integer.parseInt(ratingStr.trim());

            getReviewService().addReview(user.getId(), productId, orderId, rating, comment);
            req.getSession().setAttribute("reviewSuccess", "Thank you! Your review has been submitted.");
            resp.sendRedirect(req.getContextPath() + "/product?id=" + productId);
        } catch (ValidationException e) {
            req.getSession().setAttribute("reviewError", e.getMessage());
            if (productIdStr != null) {
                resp.sendRedirect(req.getContextPath() + "/product?id=" + productIdStr.trim());
            } else {
                resp.sendRedirect(req.getContextPath() + "/buyer/orders");
            }
        } catch (Exception e) {
            req.getSession().setAttribute("reviewError", "Failed to submit review: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/buyer/orders");
        }
    }
}
