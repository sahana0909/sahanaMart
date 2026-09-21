package com.sahana.sahanamart.service;

import com.sahana.sahanamart.dto.OrderDTO;
import com.sahana.sahanamart.model.OrderStatus;
import com.sahana.sahanamart.model.Role;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    OrderDTO checkout(Long buyerId, String shippingAddress, String paymentMethod);
    Optional<OrderDTO> getOrderById(Long orderId);
    Optional<OrderDTO> getOrderByOrderNumber(String orderNumber);
    List<OrderDTO> getBuyerOrders(Long buyerId);
    List<OrderDTO> getSellerIncomingOrders(Long sellerId);
    List<OrderDTO> getAllOrders();
    boolean updateOrderStatus(Long orderId, OrderStatus newStatus, Long requesterId, Role requesterRole);
}
