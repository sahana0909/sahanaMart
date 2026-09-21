package com.sahana.sahanamart.service;

import com.sahana.sahanamart.dto.CartItemDTO;

import java.math.BigDecimal;
import java.util.List;

public interface CartService {
    List<CartItemDTO> getCart(Long userId);
    CartItemDTO addToCart(Long userId, Long productId, int quantity);
    boolean updateQuantity(Long userId, Long cartItemId, int quantity);
    boolean removeFromCart(Long userId, Long cartItemId);
    boolean clearCart(Long userId);
    BigDecimal calculateSubtotal(Long userId);
    int getCartItemCount(Long userId);
}
