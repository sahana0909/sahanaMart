package com.sahana.sahanamart.service;

import com.sahana.sahanamart.dao.CartDAO;
import com.sahana.sahanamart.dao.ProductDAO;
import com.sahana.sahanamart.dto.CartItemDTO;
import com.sahana.sahanamart.exception.ValidationException;
import com.sahana.sahanamart.model.CartItem;
import com.sahana.sahanamart.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartDAO cartDAO;

    @Mock
    private ProductDAO productDAO;

    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartService = new CartServiceImpl(cartDAO, productDAO);
    }

    @Test
    void addToCart_Success() {
        Product product = new Product(1L, 2L, 1L, "Phone", "Desc", new BigDecimal("15000.00"), 10, null, true, null);
        when(productDAO.findById(1L)).thenReturn(Optional.of(product));
        when(cartDAO.findByUserAndProduct(5L, 1L)).thenReturn(Optional.empty());

        CartItem item = new CartItem(100L, 5L, 1L, 2, null);
        item.setProduct(product);
        when(cartDAO.addItem(5L, 1L, 2)).thenReturn(item);

        CartItemDTO dto = cartService.addToCart(5L, 1L, 2);
        assertThat(dto).isNotNull();
        assertThat(dto.getProductId()).isEqualTo(1L);
        assertThat(dto.getQuantity()).isEqualTo(2);
    }

    @Test
    void addToCart_ExceedsStock_ThrowsValidationException() {
        Product product = new Product(1L, 2L, 1L, "Phone", "Desc", new BigDecimal("15000.00"), 3, null, true, null);
        when(productDAO.findById(1L)).thenReturn(Optional.of(product));
        when(cartDAO.findByUserAndProduct(5L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.addToCart(5L, 1L, 5))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Available stock: 3");
    }
}
