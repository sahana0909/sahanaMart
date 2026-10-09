package com.sahana.sahanamart.service;

import com.sahana.sahanamart.dto.ReviewDTO;

import java.util.List;

public interface ReviewService {
    ReviewDTO addReview(Long buyerId, Long productId, Long orderId, int rating, String comment);
    List<ReviewDTO> getProductReviews(Long productId);
    List<ReviewDTO> getBuyerReviews(Long buyerId);
    boolean canUserReview(Long buyerId, Long productId);
    java.util.Optional<Long> getEligibleOrderIdForReview(Long buyerId, Long productId);
    double getAverageRating(Long productId);
    int getReviewCount(Long productId);
}
