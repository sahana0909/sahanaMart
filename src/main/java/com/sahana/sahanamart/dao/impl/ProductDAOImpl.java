package com.sahana.sahanamart.dao.impl;

import com.sahana.sahanamart.dao.ProductDAO;
import com.sahana.sahanamart.exception.DatabaseException;
import com.sahana.sahanamart.model.Category;
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

public class ProductDAOImpl implements ProductDAO {

    private Product mapRow(ResultSet rs) throws SQLException {
        Product p = new Product(
            rs.getLong("id"),
            rs.getLong("seller_id"),
            rs.getLong("category_id"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getBigDecimal("price"),
            rs.getInt("stock_qty"),
            rs.getString("image_url"),
            rs.getBoolean("is_active"),
            rs.getTimestamp("created_at")
        );
        try {
            p.setCategoryName(rs.getString("category_name"));
        } catch (SQLException ignored) {}
        try {
            p.setSellerName(rs.getString("seller_name"));
        } catch (SQLException ignored) {}
        try {
            p.setAverageRating(rs.getDouble("avg_rating"));
            p.setReviewCount(rs.getInt("review_count"));
        } catch (SQLException ignored) {}
        return p;
    }

    private static final String BASE_SELECT =
        "SELECT p.id, p.seller_id, p.category_id, p.name, p.description, p.price, p.stock_qty, " +
        "p.image_url, p.is_active, p.created_at, c.name AS category_name, u.full_name AS seller_name, " +
        "COALESCE(r.avg_rating, 0.0) AS avg_rating, COALESCE(r.review_count, 0) AS review_count " +
        "FROM products p " +
        "JOIN categories c ON p.category_id = c.id " +
        "JOIN users u ON p.seller_id = u.id " +
        "LEFT JOIN ( " +
        "    SELECT product_id, AVG(CAST(rating AS DOUBLE)) AS avg_rating, COUNT(id) AS review_count " +
        "    FROM reviews GROUP BY product_id " +
        ") r ON p.id = r.product_id ";

    @Override
    public Optional<Product> findById(Long id) {
        String sql = BASE_SELECT + "WHERE p.id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding product by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Product create(Product product) {
        String sql = "INSERT INTO products (seller_id, category_id, name, description, price, stock_qty, image_url, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, product.getSellerId());
            ps.setLong(2, product.getCategoryId());
            ps.setString(3, product.getName().trim());
            ps.setString(4, product.getDescription());
            ps.setBigDecimal(5, product.getPrice());
            ps.setInt(6, product.getStockQty() != null ? product.getStockQty() : 0);
            ps.setString(7, product.getImageUrl());
            ps.setBoolean(8, product.getActive() != null ? product.getActive() : true);

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new DatabaseException("Creating product failed, no rows affected.");
            }
            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) {
                    product.setId(gk.getLong(1));
                }
            }
            return product;
        } catch (SQLException e) {
            throw new DatabaseException("Error creating product: " + product.getName(), e);
        }
    }

    @Override
    public boolean update(Product product) {
        String sql = "UPDATE products SET category_id = ?, name = ?, description = ?, price = ?, " +
                     "stock_qty = ?, image_url = ?, is_active = ? WHERE id = ? AND seller_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, product.getCategoryId());
            ps.setString(2, product.getName().trim());
            ps.setString(3, product.getDescription());
            ps.setBigDecimal(4, product.getPrice());
            ps.setInt(5, product.getStockQty());
            ps.setString(6, product.getImageUrl());
            ps.setBoolean(7, product.getActive());
            ps.setLong(8, product.getId());
            ps.setLong(9, product.getSellerId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating product: " + product.getId(), e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting product: " + id, e);
        }
    }

    @Override
    public boolean updateStock(Long productId, int quantityDelta) {
        String sql = "UPDATE products SET stock_qty = stock_qty + ? WHERE id = ? AND (stock_qty + ?) >= 0";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantityDelta);
            ps.setLong(2, productId);
            ps.setInt(3, quantityDelta);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating stock for product: " + productId, e);
        }
    }

    @Override
    public boolean setActiveStatus(Long productId, boolean active) {
        String sql = "UPDATE products SET is_active = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, active);
            ps.setLong(2, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error setting active status for product: " + productId, e);
        }
    }

    @Override
    public List<Product> findAllActive(int limit, int offset) {
        return searchAndFilter(null, null, true, limit, offset);
    }

    @Override
    public List<Product> searchAndFilter(String keyword, Long categoryId, Boolean activeOnly, int limit, int offset) {
        List<Product> products = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT);
        sql.append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();
        if (activeOnly != null && activeOnly) {
            sql.append("AND p.is_active = TRUE ");
        }
        if (categoryId != null && categoryId > 0) {
            sql.append("AND p.category_id = ? ");
            params.add(categoryId);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (LOWER(p.name) LIKE ? OR LOWER(p.description) LIKE ?) ");
            String likePattern = "%" + keyword.trim().toLowerCase() + "%";
            params.add(likePattern);
            params.add(likePattern);
        }

        sql.append("ORDER BY p.id DESC LIMIT ? OFFSET ?");
        params.add(limit > 0 ? limit : 20);
        params.add(offset >= 0 ? offset : 0);

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error searching and filtering products", e);
        }
        return products;
    }

    @Override
    public int countSearchAndFilter(String keyword, Long categoryId, Boolean activeOnly) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM products p WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (activeOnly != null && activeOnly) {
            sql.append("AND p.is_active = TRUE ");
        }
        if (categoryId != null && categoryId > 0) {
            sql.append("AND p.category_id = ? ");
            params.add(categoryId);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (LOWER(p.name) LIKE ? OR LOWER(p.description) LIKE ?) ");
            String likePattern = "%" + keyword.trim().toLowerCase() + "%";
            params.add(likePattern);
            params.add(likePattern);
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error counting products", e);
        }
        return 0;
    }

    @Override
    public List<Product> findBySellerId(Long sellerId) {
        List<Product> products = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE p.seller_id = ? ORDER BY p.id DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding products for seller: " + sellerId, e);
        }
        return products;
    }

    @Override
    public List<Category> findAllCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT id, name, description, created_at FROM categories ORDER BY name ASC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Category(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving categories", e);
        }
        return list;
    }

    @Override
    public Optional<Category> findCategoryById(Long id) {
        String sql = "SELECT id, name, description, created_at FROM categories WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Category(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getTimestamp("created_at")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding category by id: " + id, e);
        }
        return Optional.empty();
    }
}
