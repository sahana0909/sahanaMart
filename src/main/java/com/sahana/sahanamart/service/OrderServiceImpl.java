package com.sahana.sahanamart.service;

import com.sahana.sahanamart.dao.CartDAO;
import com.sahana.sahanamart.dao.OrderDAO;
import com.sahana.sahanamart.dao.ProductDAO;
import com.sahana.sahanamart.dto.OrderDTO;
import com.sahana.sahanamart.exception.ResourceNotFoundException;
import com.sahana.sahanamart.exception.UnauthorizedException;
import com.sahana.sahanamart.exception.ValidationException;
import com.sahana.sahanamart.model.CartItem;
import com.sahana.sahanamart.model.Order;
import com.sahana.sahanamart.model.OrderItem;
import com.sahana.sahanamart.model.OrderStatus;
import com.sahana.sahanamart.model.Product;
import com.sahana.sahanamart.model.Role;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OrderServiceImpl implements OrderService {

    private final OrderDAO orderDAO;
    private final CartDAO cartDAO;
    private final ProductDAO productDAO;
    private final SecureRandom random = new SecureRandom();

    public OrderServiceImpl(OrderDAO orderDAO, CartDAO cartDAO, ProductDAO productDAO) {
        this.orderDAO = orderDAO;
        this.cartDAO = cartDAO;
        this.productDAO = productDAO;
    }

    private String generateOrderNumber() {
        String datePrefix = new SimpleDateFormat("yyyyMMdd").format(new Date());
        int randomSuffix = 1000 + random.nextInt(9000);
        return "SM-" + datePrefix + "-" + randomSuffix;
    }

    @Override
    public OrderDTO checkout(Long buyerId, String shippingAddress, String paymentMethod) {
        if (buyerId == null || buyerId <= 0) {
            throw new UnauthorizedException("Valid buyer ID required for checkout");
        }

        if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
            throw new ValidationException("shippingAddress", "Shipping address is required");
        }

        List<CartItem> cartItems = cartDAO.findByUserId(buyerId);
        if (cartItems.isEmpty()) {
            throw new ValidationException("cart", "Your cart is empty. Add items before checking out.");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem item : cartItems) {
            Product product = productDAO.findById(item.getProductId())
                .orElseThrow(() -> new ValidationException("Product not found: " + item.getProductId()));

            if (!Boolean.TRUE.equals(product.getActive())) {
                throw new ValidationException("Product '" + product.getName() + "' is no longer available.");
            }

            if (product.getStockQty() < item.getQuantity()) {
                throw new ValidationException("Insufficient stock for product '" + product.getName() + "'. Available: " + product.getStockQty());
            }

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setSellerId(product.getSellerId());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setTotalPrice(itemTotal);
            orderItem.setProductName(product.getName());
            orderItem.setProductImageUrl(product.getImageUrl());
            orderItems.add(orderItem);
        }

        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setBuyerId(buyerId);
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING);
        order.setShippingAddress(shippingAddress.trim());
        order.setPaymentStatus("COMPLETED"); // Mock payment confirmation step

        Order savedOrder = orderDAO.createOrder(order, orderItems);

        // Clear user cart upon successful order
        cartDAO.clearCart(buyerId);

        return getOrderById(savedOrder.getId()).orElse(OrderDTO.fromOrder(savedOrder));
    }

    @Override
    public Optional<OrderDTO> getOrderById(Long orderId) {
        if (orderId == null || orderId <= 0) {
            return Optional.empty();
        }
        return orderDAO.findById(orderId).map(OrderDTO::fromOrder);
    }

    @Override
    public Optional<OrderDTO> getOrderByOrderNumber(String orderNumber) {
        if (orderNumber == null || orderNumber.trim().isEmpty()) {
            return Optional.empty();
        }
        return orderDAO.findByOrderNumber(orderNumber.trim()).map(OrderDTO::fromOrder);
    }

    @Override
    public List<OrderDTO> getBuyerOrders(Long buyerId) {
        if (buyerId == null || buyerId <= 0) {
            return List.of();
        }
        return orderDAO.findByBuyerId(buyerId).stream()
            .map(OrderDTO::fromOrder)
            .collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getSellerIncomingOrders(Long sellerId) {
        if (sellerId == null || sellerId <= 0) {
            return List.of();
        }
        return orderDAO.findBySellerId(sellerId).stream()
            .map(OrderDTO::fromOrder)
            .collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        return orderDAO.findAll().stream()
            .map(OrderDTO::fromOrder)
            .collect(Collectors.toList());
    }

    @Override
    public boolean updateOrderStatus(Long orderId, OrderStatus newStatus, Long requesterId, Role requesterRole) {
        Order order = orderDAO.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        // Authorization check: Admin can update any order; Seller can update orders containing their items
        if (requesterRole != Role.ADMIN) {
            boolean isSellerOfOrder = order.getItems().stream()
                .anyMatch(item -> item.getSellerId().equals(requesterId));
            if (!isSellerOfOrder) {
                throw new UnauthorizedException("You are not authorized to update this order's status");
            }
        }

        // Validate state machine transition (O2 workflow)
        if (!order.getStatus().canTransitionTo(newStatus)) {
            throw new ValidationException("Invalid status transition from " + order.getStatus() + " to " + newStatus);
        }

        return orderDAO.updateStatus(orderId, newStatus);
    }
}
