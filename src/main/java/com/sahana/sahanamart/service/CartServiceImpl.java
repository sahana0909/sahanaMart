package com.sahana.sahanamart.service;

import com.sahana.sahanamart.dao.CartDAO;
import com.sahana.sahanamart.dao.ProductDAO;
import com.sahana.sahanamart.dto.CartItemDTO;
import com.sahana.sahanamart.exception.ResourceNotFoundException;
import com.sahana.sahanamart.exception.ValidationException;
import com.sahana.sahanamart.model.CartItem;
import com.sahana.sahanamart.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class CartServiceImpl implements CartService {

    private final CartDAO cartDAO;
    private final ProductDAO productDAO;

    public CartServiceImpl(CartDAO cartDAO, ProductDAO productDAO) {
        this.cartDAO = cartDAO;
        this.productDAO = productDAO;
    }

    @Override
    public List<CartItemDTO> getCart(Long userId) {
        if (userId == null || userId <= 0) {
            return List.of();
        }
        return cartDAO.findByUserId(userId).stream()
            .map(CartItemDTO::fromCartItem)
            .collect(Collectors.toList());
    }

    @Override
    public CartItemDTO addToCart(Long userId, Long productId, int quantity) {
        if (userId == null || userId <= 0) {
            throw new ValidationException("User ID must be provided");
        }
        if (productId == null || productId <= 0) {
            throw new ValidationException("Product ID must be provided");
        }
        if (quantity <= 0) {
            throw new ValidationException("quantity", "Quantity must be at least 1");
        }

        Product product = productDAO.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new ValidationException("product", "This product is currently inactive and cannot be purchased");
        }

        int requestedTotal = quantity;
        var existing = cartDAO.findByUserAndProduct(userId, productId);
        if (existing.isPresent()) {
            requestedTotal += existing.get().getQuantity();
        }

        if (requestedTotal > product.getStockQty()) {
            throw new ValidationException("quantity",
                "Cannot add " + quantity + " items. Available stock: " + product.getStockQty() +
                (existing.isPresent() ? " (" + existing.get().getQuantity() + " already in your cart)" : ""));
        }

        CartItem item = cartDAO.addItem(userId, productId, quantity);
        item.setProduct(product);
        return CartItemDTO.fromCartItem(item);
    }

    @Override
    public boolean updateQuantity(Long userId, Long cartItemId, int quantity) {
        if (quantity <= 0) {
            return cartDAO.removeItem(cartItemId, userId);
        }

        // Validate stock
        List<CartItem> userCart = cartDAO.findByUserId(userId);
        CartItem target = userCart.stream()
            .filter(i -> i.getId().equals(cartItemId))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (target.getProduct() != null && quantity > target.getProduct().getStockQty()) {
            throw new ValidationException("quantity", "Requested quantity exceeds available stock (" + target.getProduct().getStockQty() + ")");
        }

        return cartDAO.updateQuantity(cartItemId, quantity);
    }

    @Override
    public boolean removeFromCart(Long userId, Long cartItemId) {
        return cartDAO.removeItem(cartItemId, userId);
    }

    @Override
    public boolean clearCart(Long userId) {
        return cartDAO.clearCart(userId);
    }

    @Override
    public BigDecimal calculateSubtotal(Long userId) {
        return cartDAO.findByUserId(userId).stream()
            .map(CartItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public int getCartItemCount(Long userId) {
        return cartDAO.countItems(userId);
    }
}
