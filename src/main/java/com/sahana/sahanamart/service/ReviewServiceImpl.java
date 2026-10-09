package com.sahana.sahanamart.service;

import com.sahana.sahanamart.dao.OrderDAO;
import com.sahana.sahanamart.dao.ReviewDAO;
import com.sahana.sahanamart.dto.ReviewDTO;
import com.sahana.sahanamart.exception.UnauthorizedException;
import com.sahana.sahanamart.exception.ValidationException;
import com.sahana.sahanamart.model.Order;
import com.sahana.sahanamart.model.OrderStatus;
import com.sahana.sahanamart.model.Review;
import com.sahana.sahanamart.util.ValidationUtil;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReviewServiceImpl implements ReviewService {

    private final ReviewDAO reviewDAO;
    private final OrderDAO orderDAO;

    public ReviewServiceImpl(ReviewDAO reviewDAO, OrderDAO orderDAO) {
        this.reviewDAO = reviewDAO;
        this.orderDAO = orderDAO;
    }

    @Override
    public ReviewDTO addReview(Long buyerId, Long productId, Long orderId, int rating, String comment) {
        if (buyerId == null || buyerId <= 0) {
            throw new UnauthorizedException("You must be logged in as a buyer to leave a review");
        }
        ValidationUtil.validateReview(rating, comment);

        // Verify that the order exists, belongs to this buyer, and is DELIVERED
        var orderOpt = orderDAO.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new ValidationException("orderId", "Order not found");
        }
        var order = orderOpt.get();
        if (!order.getBuyerId().equals(buyerId)) {
            throw new UnauthorizedException("You cannot review an order placed by someone else");
        }

        // Verify product was part of this order
        boolean productInOrder = order.getItems().stream()
            .anyMatch(i -> i.getProductId().equals(productId));
        if (!productInOrder) {
            throw new ValidationException("productId", "This product is not part of order " + order.getOrderNumber());
        }

        // Check if already reviewed for this order and product
        if (reviewDAO.findByBuyerAndOrderAndProduct(buyerId, orderId, productId).isPresent()) {
            throw new ValidationException("review", "You have already reviewed this product for this order");
        }

        Review review = new Review();
        review.setBuyerId(buyerId);
        review.setProductId(productId);
        review.setOrderId(orderId);
        review.setRating(rating);
        review.setComment(comment != null ? comment.trim() : "");

        Review saved = reviewDAO.create(review);
        return ReviewDTO.fromReview(saved);
    }

    @Override
    public List<ReviewDTO> getProductReviews(Long productId) {
        if (productId == null || productId <= 0) {
            return List.of();
        }
        return reviewDAO.findByProductId(productId).stream()
            .map(ReviewDTO::fromReview)
            .collect(Collectors.toList());
    }

    @Override
    public List<ReviewDTO> getBuyerReviews(Long buyerId) {
        if (buyerId == null || buyerId <= 0) {
            return List.of();
        }
        return reviewDAO.findByBuyerId(buyerId).stream()
            .map(ReviewDTO::fromReview)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Long> getEligibleOrderIdForReview(Long buyerId, Long productId) {
        if (buyerId == null || productId == null) {
            return Optional.empty();
        }
        List<Order> buyerOrders = orderDAO.findByBuyerId(buyerId);
        for (Order order : buyerOrders) {
            if (order.getStatus() == OrderStatus.DELIVERED) {
                boolean containsProduct = order.getItems() != null && order.getItems().stream()
                    .anyMatch(i -> i.getProductId().equals(productId));
                if (containsProduct) {
                    if (reviewDAO.findByBuyerAndOrderAndProduct(buyerId, order.getId(), productId).isEmpty()) {
                        return Optional.of(order.getId());
                    }
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean canUserReview(Long buyerId, Long productId) {
        return getEligibleOrderIdForReview(buyerId, productId).isPresent();
    }

    @Override
    public double getAverageRating(Long productId) {
        return reviewDAO.getAverageRating(productId);
    }

    @Override
    public int getReviewCount(Long productId) {
        return reviewDAO.getReviewCount(productId);
    }
}
