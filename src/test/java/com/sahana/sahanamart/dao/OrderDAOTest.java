package com.sahana.sahanamart.dao;

import com.sahana.sahanamart.dao.impl.OrderDAOImpl;
import com.sahana.sahanamart.dao.impl.ProductDAOImpl;
import com.sahana.sahanamart.dao.impl.UserDAOImpl;
import com.sahana.sahanamart.model.Order;
import com.sahana.sahanamart.model.OrderItem;
import com.sahana.sahanamart.model.OrderStatus;
import com.sahana.sahanamart.model.Product;
import com.sahana.sahanamart.model.Role;
import com.sahana.sahanamart.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderDAOTest extends BaseDAOTest {

    private OrderDAO orderDAO;
    private ProductDAO productDAO;
    private Long buyerId;
    private Long sellerId;
    private Product product;

    @BeforeEach
    void setUp() {
        orderDAO = new OrderDAOImpl();
        productDAO = new ProductDAOImpl();
        UserDAO userDAO = new UserDAOImpl();

        User buyer = userDAO.findByEmail("order_buyer@example.com")
            .orElseGet(() -> userDAO.create(new User(null, "order_buyer@example.com", "hash", "Order Buyer", Role.BUYER, "123", null)));
        buyerId = buyer.getId();

        User seller = userDAO.findByEmail("order_seller@example.com")
            .orElseGet(() -> userDAO.create(new User(null, "order_seller@example.com", "hash", "Order Seller", Role.SELLER, "123", null)));
        sellerId = seller.getId();

        product = productDAO.create(new Product(null, sellerId, 1L, "Orderable Item", "Desc",
            new BigDecimal("200.00"), 10, null, true, null));
    }

    @Test
    void testCreateOrderAndDeductStock() {
        String orderNum = "TEST-" + System.currentTimeMillis();
        Order order = new Order(null, orderNum, buyerId, new BigDecimal("400.00"),
            OrderStatus.PENDING, "Chennai", "COMPLETED", null);

        OrderItem item = new OrderItem(null, null, product.getId(), sellerId, 2,
            new BigDecimal("200.00"), new BigDecimal("400.00"), null);

        Order created = orderDAO.createOrder(order, List.of(item));
        assertThat(created.getId()).isNotNull();

        // Check stock was deducted atomically
        Product refreshed = productDAO.findById(product.getId()).orElseThrow();
        assertThat(refreshed.getStockQty()).isEqualTo(8);

        // Check find by buyer
        List<Order> buyerOrders = orderDAO.findByBuyerId(buyerId);
        assertThat(buyerOrders).isNotEmpty();
        assertThat(buyerOrders.get(0).getItems()).isNotEmpty();

        // Check status transition
        boolean statusUpdated = orderDAO.updateStatus(created.getId(), OrderStatus.CONFIRMED);
        assertThat(statusUpdated).isTrue();
    }
}
