package com.sahana.sahanamart.dao.impl;

import com.sahana.sahanamart.dao.ReviewDAO;
import com.sahana.sahanamart.exception.DatabaseException;
import com.sahana.sahanamart.model.Review;
import com.sahana.sahanamart.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReviewDAOImpl implements ReviewDAO {

    private Review mapRow(ResultSet rs) throws SQLException {
        Review r = new Review(
            rs.getLong("id"),
            rs.getLong("product_id"),
            rs.getLong("buyer_id"),
            rs.getLong("order_id"),
            rs.getInt("rating"),
            rs.getString("comment"),
            rs.getTimestamp("created_at")
        );
        try {
            r.setBuyerName(rs.getString("buyer_name"));
        } catch (SQLException ignored) {}
        try {
            r.setProductName(rs.getString("product_name"));
        } catch (SQLException ignored) {}
        return r;
    }

    private static final String BASE_SELECT =
        "SELECT r.id, r.product_id, r.buyer_id, r.order_id, r.rating, r.comment, r.created_at, " +
        "u.full_name AS buyer_name, p.name AS product_name " +
        "FROM reviews r " +
        "JOIN users u ON r.buyer_id = u.id " +
        "JOIN products p ON r.product_id = p.id ";

    @Override
    public Review create(Review review) {
        String sql = "INSERT INTO reviews (product_id, buyer_id, order_id, rating, comment) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, review.getProductId());
            ps.setLong(2, review.getBuyerId());
            ps.setLong(3, review.getOrderId());
            ps.setInt(4, review.getRating());
            ps.setString(5, review.getComment());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new DatabaseException("Creating review failed, no rows affected.");
            }
            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) {
                    review.setId(gk.getLong(1));
                }
            }
            return review;
        } catch (SQLException e) {
            throw new DatabaseException("Error creating review", e);
        }
    }

    @Override
    public List<Review> findByProductId(Long productId) {
        List<Review> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE r.product_id = ? ORDER BY r.id DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding reviews for product: " + productId, e);
        }
        return list;
    }

    @Override
    public List<Review> findByBuyerId(Long buyerId) {
        List<Review> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE r.buyer_id = ? ORDER BY r.id DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, buyerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding reviews for buyer: " + buyerId, e);
        }
        return list;
    }

    @Override
    public Optional<Review> findByBuyerAndOrderAndProduct(Long buyerId, Long orderId, Long productId) {
        String sql = BASE_SELECT + "WHERE r.buyer_id = ? AND r.order_id = ? AND r.product_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, buyerId);
            ps.setLong(2, orderId);
            ps.setLong(3, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error checking existing review", e);
        }
        return Optional.empty();
    }

    @Override
    public double getAverageRating(Long productId) {
        String sql = "SELECT COALESCE(AVG(CAST(rating AS DOUBLE)), 0.0) FROM reviews WHERE product_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error calculating average rating for product: " + productId, e);
        }
        return 0.0;
    }

    @Override
    public int getReviewCount(Long productId) {
        String sql = "SELECT COUNT(*) FROM reviews WHERE product_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error counting reviews for product: " + productId, e);
        }
        return 0;
    }
}
