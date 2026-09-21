package com.sahana.sahanamart.dao.impl;

import com.sahana.sahanamart.dao.OrderDAO;
import com.sahana.sahanamart.exception.DatabaseException;
import com.sahana.sahanamart.exception.ValidationException;
import com.sahana.sahanamart.model.Order;
import com.sahana.sahanamart.model.OrderItem;
import com.sahana.sahanamart.model.OrderStatus;
import com.sahana.sahanamart.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderDAOImpl implements OrderDAO {

    private Order mapOrder(ResultSet rs) throws SQLException {
        Order o = new Order(
            rs.getLong("id"),
            rs.getString("order_number"),
            rs.getLong("buyer_id"),
            rs.getBigDecimal("total_amount"),
            OrderStatus.fromString(rs.getString("status")),
            rs.getString("shipping_address"),
            rs.getString("payment_status"),
            rs.getTimestamp("created_at")
        );
        try {
            o.setBuyerName(rs.getString("buyer_name"));
        } catch (SQLException ignored) {}
        try {
            o.setBuyerEmail(rs.getString("buyer_email"));
        } catch (SQLException ignored) {}
        try {
            o.setTrackingNumber(rs.getString("tracking_number"));
        } catch (SQLException ignored) {}
        return o;
    }

    private OrderItem mapOrderItem(ResultSet rs) throws SQLException {
        OrderItem item = new OrderItem(
            rs.getLong("id"),
            rs.getLong("order_id"),
            rs.getLong("product_id"),
            rs.getLong("seller_id"),
            rs.getInt("quantity"),
            rs.getBigDecimal("unit_price"),
            rs.getBigDecimal("total_price"),
            rs.getTimestamp("created_at")
        );
        try {
            item.setProductName(rs.getString("product_name"));
        } catch (SQLException ignored) {}
        try {
            item.setProductImageUrl(rs.getString("product_image"));
        } catch (SQLException ignored) {}
        return item;
    }

    private void loadItemsForOrder(Connection conn, Order order) throws SQLException {
        String sql = "SELECT oi.id, oi.order_id, oi.product_id, oi.seller_id, oi.quantity, oi.unit_price, " +
                     "oi.total_price, oi.created_at, p.name AS product_name, p.image_url AS product_image " +
                     "FROM order_items oi " +
                     "JOIN products p ON oi.product_id = p.id " +
                     "WHERE oi.order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, order.getId());
            try (ResultSet rs = ps.executeQuery()) {
                List<OrderItem> items = new ArrayList<>();
                while (rs.next()) {
                    items.add(mapOrderItem(rs));
                }
                order.setItems(items);
            }
        }
    }

    @Override
    public Optional<Order> findById(Long id) {
        String sql = "SELECT o.id, o.order_number, o.buyer_id, o.total_amount, o.status, o.shipping_address, " +
                     "o.payment_status, o.created_at, o.tracking_number, u.full_name AS buyer_name, u.email AS buyer_email " +
                     "FROM orders o " +
                     "JOIN users u ON o.buyer_id = u.id " +
                     "WHERE o.id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order order = mapOrder(rs);
                    loadItemsForOrder(conn, order);
                    return Optional.of(order);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding order by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Order> findByOrderNumber(String orderNumber) {
        String sql = "SELECT o.id, o.order_number, o.buyer_id, o.total_amount, o.status, o.shipping_address, " +
                     "o.payment_status, o.created_at, o.tracking_number, u.full_name AS buyer_name, u.email AS buyer_email " +
                     "FROM orders o " +
                     "JOIN users u ON o.buyer_id = u.id " +
                     "WHERE o.order_number = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order order = mapOrder(rs);
                    loadItemsForOrder(conn, order);
                    return Optional.of(order);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding order by order number: " + orderNumber, e);
        }
        return Optional.empty();
    }

    @Override
    public Order createOrder(Order order, List<OrderItem> items) {
        String insertOrderSql = "INSERT INTO orders (order_number, buyer_id, total_amount, status, shipping_address, payment_status) " +
                                "VALUES (?, ?, ?, ?, ?, ?)";
        String insertItemSql = "INSERT INTO order_items (order_id, product_id, seller_id, quantity, unit_price, total_price) " +
                               "VALUES (?, ?, ?, ?, ?, ?)";
        String checkStockSql = "SELECT stock_qty, name FROM products WHERE id = ? FOR UPDATE";
        String deductStockSql = "UPDATE products SET stock_qty = stock_qty - ? WHERE id = ?";

        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            // Verify stock and reserve
            for (OrderItem item : items) {
                try (PreparedStatement checkPs = conn.prepareStatement(checkStockSql)) {
                    checkPs.setLong(1, item.getProductId());
                    try (ResultSet rs = checkPs.executeQuery()) {
                        if (!rs.next()) {
                            throw new ValidationException("Product not found: ID " + item.getProductId());
                        }
                        int currentStock = rs.getInt("stock_qty");
                        String pName = rs.getString("name");
                        if (currentStock < item.getQuantity()) {
                            throw new ValidationException("Insufficient stock for product '" + pName + "'. Available: " + currentStock);
                        }
                    }
                }

                try (PreparedStatement deductPs = conn.prepareStatement(deductStockSql)) {
                    deductPs.setInt(1, item.getQuantity());
                    deductPs.setLong(2, item.getProductId());
                    deductPs.executeUpdate();
                }
            }

            // Insert Order
            try (PreparedStatement psOrder = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS)) {
                psOrder.setString(1, order.getOrderNumber());
                psOrder.setLong(2, order.getBuyerId());
                psOrder.setBigDecimal(3, order.getTotalAmount());
                psOrder.setString(4, order.getStatus().name());
                psOrder.setString(5, order.getShippingAddress());
                psOrder.setString(6, order.getPaymentStatus());
                psOrder.executeUpdate();

                try (ResultSet gk = psOrder.getGeneratedKeys()) {
                    if (gk.next()) {
                        order.setId(gk.getLong(1));
                    }
                }
            }

            // Insert Order Items
            try (PreparedStatement psItem = conn.prepareStatement(insertItemSql, Statement.RETURN_GENERATED_KEYS)) {
                for (OrderItem item : items) {
                    psItem.setLong(1, order.getId());
                    psItem.setLong(2, item.getProductId());
                    psItem.setLong(3, item.getSellerId());
                    psItem.setInt(4, item.getQuantity());
                    psItem.setBigDecimal(5, item.getUnitPrice());
                    psItem.setBigDecimal(6, item.getTotalPrice());
                    psItem.addBatch();
                }
                psItem.executeBatch();
            }

            conn.commit();
            order.setItems(items);
            return order;
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    // suppress
                }
            }
            if (e instanceof ValidationException) {
                throw (ValidationException) e;
            }
            throw new DatabaseException("Failed to place order: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    @Override
    public boolean updateStatus(Long orderId, OrderStatus newStatus) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus.name());
            ps.setLong(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating order status: " + orderId, e);
        }
    }

    @Override
    public List<Order> findByBuyerId(Long buyerId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.id, o.order_number, o.buyer_id, o.total_amount, o.status, o.shipping_address, " +
                     "o.payment_status, o.created_at, o.tracking_number, u.full_name AS buyer_name, u.email AS buyer_email " +
                     "FROM orders o " +
                     "JOIN users u ON o.buyer_id = u.id " +
                     "WHERE o.buyer_id = ? ORDER BY o.id DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, buyerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order o = mapOrder(rs);
                    loadItemsForOrder(conn, o);
                    list.add(o);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding orders for buyer: " + buyerId, e);
        }
        return list;
    }

    @Override
    public List<Order> findBySellerId(Long sellerId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT DISTINCT o.id, o.order_number, o.buyer_id, o.total_amount, o.status, o.shipping_address, " +
                     "o.payment_status, o.created_at, o.tracking_number, u.full_name AS buyer_name, u.email AS buyer_email " +
                     "FROM orders o " +
                     "JOIN users u ON o.buyer_id = u.id " +
                     "JOIN order_items oi ON o.id = oi.order_id " +
                     "WHERE oi.seller_id = ? ORDER BY o.id DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order o = mapOrder(rs);
                    loadItemsForOrder(conn, o);
                    list.add(o);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding orders for seller: " + sellerId, e);
        }
        return list;
    }

    @Override
    public List<Order> findAll() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.id, o.order_number, o.buyer_id, o.total_amount, o.status, o.shipping_address, " +
                     "o.payment_status, o.created_at, o.tracking_number, u.full_name AS buyer_name, u.email AS buyer_email " +
                     "FROM orders o " +
                     "JOIN users u ON o.buyer_id = u.id " +
                     "ORDER BY o.id DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Order o = mapOrder(rs);
                loadItemsForOrder(conn, o);
                list.add(o);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding all orders", e);
        }
        return list;
    }

    @Override
    public boolean hasPurchasedAndCompleted(Long buyerId, Long productId) {
        String sql = "SELECT COUNT(*) FROM orders o " +
                     "JOIN order_items oi ON o.id = oi.order_id " +
                     "WHERE o.buyer_id = ? AND oi.product_id = ? AND o.status = 'DELIVERED'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, buyerId);
            ps.setLong(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error checking purchase status", e);
        }
        return false;
    }
}
