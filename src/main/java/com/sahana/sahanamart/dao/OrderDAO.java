package com.sahana.sahanamart.dao;

import com.sahana.sahanamart.model.Order;
import com.sahana.sahanamart.model.OrderItem;
import com.sahana.sahanamart.model.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderDAO {
    Optional<Order> findById(Long id);
    Optional<Order> findByOrderNumber(String orderNumber);
    Order createOrder(Order order, List<OrderItem> items);
    boolean updateStatus(Long orderId, OrderStatus newStatus);
    List<Order> findByBuyerId(Long buyerId);
    List<Order> findBySellerId(Long sellerId);
    List<Order> findAll();
    boolean hasPurchasedAndCompleted(Long buyerId, Long productId);
}
