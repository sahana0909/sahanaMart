package com.sahana.sahanamart.dao;

import com.sahana.sahanamart.model.CartItem;

import java.util.List;
import java.util.Optional;

public interface CartDAO {
    List<CartItem> findByUserId(Long userId);
    Optional<CartItem> findByUserAndProduct(Long userId, Long productId);
    CartItem addItem(Long userId, Long productId, int quantity);
    boolean updateQuantity(Long cartItemId, int quantity);
    boolean removeItem(Long cartItemId, Long userId);
    boolean clearCart(Long userId);
    int countItems(Long userId);
}
