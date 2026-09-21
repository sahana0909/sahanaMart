package com.sahana.sahanamart.dao.impl;

import com.sahana.sahanamart.dao.CartDAO;
import com.sahana.sahanamart.exception.DatabaseException;
import com.sahana.sahanamart.model.CartItem;
import com.sahana.sahanamart.model.Product;
import com.sahana.sahanamart.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CartDAOImpl implements CartDAO {

    private CartItem mapRow(ResultSet rs) throws SQLException {
        CartItem item = new CartItem(
            rs.getLong("id"),
            rs.getLong("user_id"),
            rs.getLong("product_id"),
            rs.getInt("quantity"),
            rs.getTimestamp("created_at")
        );
        Product p = new Product();
        p.setId(rs.getLong("product_id"));
        p.setName(rs.getString("product_name"));
        p.setPrice(rs.getBigDecimal("product_price"));
        p.setStockQty(rs.getInt("product_stock"));
        p.setImageUrl(rs.getString("product_image"));
        p.setActive(rs.getBoolean("product_active"));
        item.setProduct(p);
        return item;
    }

    private static final String BASE_SELECT =
        "SELECT c.id, c.user_id, c.product_id, c.quantity, c.created_at, " +
        "p.name AS product_name, p.price AS product_price, p.stock_qty AS product_stock, " +
        "p.image_url AS product_image, p.is_active AS product_active " +
        "FROM cart_items c " +
        "JOIN products p ON c.product_id = p.id ";

    @Override
    public List<CartItem> findByUserId(Long userId) {
        List<CartItem> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE c.user_id = ? ORDER BY c.id ASC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding cart items for user: " + userId, e);
        }
        return list;
    }

    @Override
    public Optional<CartItem> findByUserAndProduct(Long userId, Long productId) {
        String sql = BASE_SELECT + "WHERE c.user_id = ? AND c.product_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding cart item for user " + userId + " and product " + productId, e);
        }
        return Optional.empty();
    }

    @Override
    public CartItem addItem(Long userId, Long productId, int quantity) {
        Optional<CartItem> existing = findByUserAndProduct(userId, productId);
        if (existing.isPresent()) {
            CartItem item = existing.get();
            int newQty = item.getQuantity() + quantity;
            updateQuantity(item.getId(), newQty);
            item.setQuantity(newQty);
            return item;
        }

        String sql = "INSERT INTO cart_items (user_id, product_id, quantity) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            ps.setInt(3, quantity);
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) {
                    Long id = gk.getLong(1);
                    return findByUserAndProduct(userId, productId).orElse(new CartItem(id, userId, productId, quantity, null));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error adding item to cart", e);
        }
        return new CartItem(null, userId, productId, quantity, null);
    }

    @Override
    public boolean updateQuantity(Long cartItemId, int quantity) {
        String sql = "UPDATE cart_items SET quantity = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setLong(2, cartItemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating cart quantity", e);
        }
    }

    @Override
    public boolean removeItem(Long cartItemId, Long userId) {
        String sql = "DELETE FROM cart_items WHERE id = ? AND user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, cartItemId);
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error removing cart item: " + cartItemId, e);
        }
    }

    @Override
    public boolean clearCart(Long userId) {
        String sql = "DELETE FROM cart_items WHERE user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            return ps.executeUpdate() >= 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error clearing cart for user: " + userId, e);
        }
    }

    @Override
    public int countItems(Long userId) {
        String sql = "SELECT COALESCE(SUM(quantity), 0) FROM cart_items WHERE user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error counting cart items for user: " + userId, e);
        }
        return 0;
    }
}
