package com.sahana.sahanamart.service;

import com.sahana.sahanamart.dao.CartDAO;
import com.sahana.sahanamart.dao.OrderDAO;
import com.sahana.sahanamart.dao.ProductDAO;
import com.sahana.sahanamart.dto.OrderDTO;
import com.sahana.sahanamart.exception.ValidationException;
import com.sahana.sahanamart.model.CartItem;
import com.sahana.sahanamart.model.Order;
import com.sahana.sahanamart.model.OrderStatus;
import com.sahana.sahanamart.model.Product;
import com.sahana.sahanamart.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderDAO orderDAO;
    @Mock
    private CartDAO cartDAO;
    @Mock
    private ProductDAO productDAO;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderDAO, cartDAO, productDAO);
    }

    @Test
    void checkout_EmptyCart_ThrowsValidationException() {
        when(cartDAO.findByUserId(10L)).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> orderService.checkout(10L, "Chennai", "UPI"))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("cart is empty");
    }

    @Test
    void checkout_Success() {
        Product p = new Product(1L, 2L, 1L, "Book", "Desc", new BigDecimal("500.00"), 10, null, true, null);
        CartItem item = new CartItem(1L, 10L, 1L, 2, null);
        item.setProduct(p);

        when(cartDAO.findByUserId(10L)).thenReturn(List.of(item));
        when(productDAO.findById(1L)).thenReturn(Optional.of(p));

        when(orderDAO.createOrder(any(Order.class), anyList())).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(99L);
            return o;
        });

        when(orderDAO.findById(99L)).thenAnswer(invocation -> {
            Order o = new Order(99L, "SM-2026-99", 10L, new BigDecimal("1000.00"), OrderStatus.PENDING, "Chennai", "COMPLETED", null);
            return Optional.of(o);
        });

        OrderDTO result = orderService.checkout(10L, "Chennai", "UPI");
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(99L);
        verify(cartDAO).clearCart(10L);
    }

    @Test
    void updateOrderStatus_InvalidTransition_ThrowsValidationException() {
        Order order = new Order(1L, "SM-1", 10L, new BigDecimal("100.00"), OrderStatus.DELIVERED, "Address", "COMPLETED", null);
        when(orderDAO.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.updateOrderStatus(1L, OrderStatus.PENDING, 2L, Role.ADMIN))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Invalid status transition");
    }
}
