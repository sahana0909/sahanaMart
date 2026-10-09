package com.sahana.sahanamart.service;

import com.sahana.sahanamart.dao.OrderDAO;
import com.sahana.sahanamart.dao.ReviewDAO;
import com.sahana.sahanamart.dto.ReviewDTO;
import com.sahana.sahanamart.exception.ValidationException;
import com.sahana.sahanamart.model.Order;
import com.sahana.sahanamart.model.OrderItem;
import com.sahana.sahanamart.model.OrderStatus;
import com.sahana.sahanamart.model.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private ReviewDAO reviewDAO;

    @Mock
    private OrderDAO orderDAO;

    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        reviewService = new ReviewServiceImpl(reviewDAO, orderDAO);
    }

    @Test
    void addReview_Success() {
        Order order = new Order(10L, "SM-10", 3L, new BigDecimal("500.00"), OrderStatus.DELIVERED, "Chennai", "COMPLETED", null);
        OrderItem item = new OrderItem(1L, 10L, 5L, 2L, 1, new BigDecimal("500.00"), new BigDecimal("500.00"), null);
        order.setItems(List.of(item));

        when(orderDAO.findById(10L)).thenReturn(Optional.of(order));
        when(reviewDAO.findByBuyerAndOrderAndProduct(3L, 10L, 5L)).thenReturn(Optional.empty());

        when(reviewDAO.create(any(Review.class))).thenAnswer(invocation -> {
            Review r = invocation.getArgument(0);
            r.setId(100L);
            return r;
        });

        ReviewDTO dto = reviewService.addReview(3L, 5L, 10L, 5, "Great product!");
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(100L);
        assertThat(dto.getRating()).isEqualTo(5);
    }

    @Test
    void addReview_InvalidRating_ThrowsValidationException() {
        assertThatThrownBy(() -> reviewService.addReview(3L, 5L, 10L, 6, "Too high rating"))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Rating must be between 1 and 5 stars");
    }

    @Test
    void addReview_DuplicateReview_ThrowsValidationException() {
        Order order = new Order(10L, "SM-10", 3L, new BigDecimal("500.00"), OrderStatus.DELIVERED, "Chennai", "COMPLETED", null);
        OrderItem item = new OrderItem(1L, 10L, 5L, 2L, 1, new BigDecimal("500.00"), new BigDecimal("500.00"), null);
        order.setItems(List.of(item));

        when(orderDAO.findById(10L)).thenReturn(Optional.of(order));
        when(reviewDAO.findByBuyerAndOrderAndProduct(3L, 10L, 5L)).thenReturn(Optional.of(new Review()));

        assertThatThrownBy(() -> reviewService.addReview(3L, 5L, 10L, 5, "Another review"))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("already reviewed");
    }

    @Test
    void testGetEligibleOrderIdForReview() {
        Order deliveredOrder = new Order(10L, "SM-10", 3L, new BigDecimal("500.00"), OrderStatus.DELIVERED, "Chennai", "COMPLETED", null);
        OrderItem item = new OrderItem(1L, 10L, 5L, 2L, 1, new BigDecimal("500.00"), new BigDecimal("500.00"), null);
        deliveredOrder.setItems(List.of(item));

        when(orderDAO.findByBuyerId(3L)).thenReturn(List.of(deliveredOrder));
        when(reviewDAO.findByBuyerAndOrderAndProduct(3L, 10L, 5L)).thenReturn(Optional.empty());

        Optional<Long> eligibleId = reviewService.getEligibleOrderIdForReview(3L, 5L);
        assertThat(eligibleId).isPresent().contains(10L);
        assertThat(reviewService.canUserReview(3L, 5L)).isTrue();
    }

    @Test
    void testGetEligibleOrderIdForReview_AlreadyReviewed() {
        Order deliveredOrder = new Order(10L, "SM-10", 3L, new BigDecimal("500.00"), OrderStatus.DELIVERED, "Chennai", "COMPLETED", null);
        OrderItem item = new OrderItem(1L, 10L, 5L, 2L, 1, new BigDecimal("500.00"), new BigDecimal("500.00"), null);
        deliveredOrder.setItems(List.of(item));

        when(orderDAO.findByBuyerId(3L)).thenReturn(List.of(deliveredOrder));
        when(reviewDAO.findByBuyerAndOrderAndProduct(3L, 10L, 5L)).thenReturn(Optional.of(new Review()));

        Optional<Long> eligibleId = reviewService.getEligibleOrderIdForReview(3L, 5L);
        assertThat(eligibleId).isEmpty();
        assertThat(reviewService.canUserReview(3L, 5L)).isFalse();
    }
}
