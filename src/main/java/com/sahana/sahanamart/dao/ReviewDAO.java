package com.sahana.sahanamart.dao;

import com.sahana.sahanamart.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewDAO {
    Review create(Review review);
    List<Review> findByProductId(Long productId);
    List<Review> findByBuyerId(Long buyerId);
    Optional<Review> findByBuyerAndOrderAndProduct(Long buyerId, Long orderId, Long productId);
    double getAverageRating(Long productId);
    int getReviewCount(Long productId);
}
