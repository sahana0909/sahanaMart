package com.sahana.sahanamart.dao;

import com.sahana.sahanamart.dao.impl.ProductDAOImpl;
import com.sahana.sahanamart.dao.impl.UserDAOImpl;
import com.sahana.sahanamart.model.Product;
import com.sahana.sahanamart.model.Role;
import com.sahana.sahanamart.model.User;
import com.sahana.sahanamart.util.DatabaseUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductDAOTest extends BaseDAOTest {

    private ProductDAO productDAO;
    private Long sellerId;
    private Long categoryId;

    @BeforeEach
    void setUp() throws Exception {
        productDAO = new ProductDAOImpl();
        UserDAO userDAO = new UserDAOImpl();

        // Ensure category and seller exist
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("MERGE INTO categories (id, name, description) KEY(id) VALUES (1, 'Tech', 'Tech items')");
        }
        categoryId = 1L;

        User seller = userDAO.findByEmail("seller_test@example.com")
            .orElseGet(() -> userDAO.create(new User(null, "seller_test@example.com", "hash", "Seller Test", Role.SELLER, "123", null)));
        sellerId = seller.getId();
    }

    @Test
    void testCreateAndFindProduct() {
        Product p = new Product(null, sellerId, categoryId, "Test Gadget", "Description",
            new BigDecimal("1299.50"), 10, "https://example.com/img.jpg", true, null);

        Product created = productDAO.create(p);
        assertThat(created.getId()).isNotNull();

        var found = productDAO.findById(created.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Gadget");
        assertThat(found.get().getPrice()).isEqualByComparingTo("1299.50");
        assertThat(found.get().getStockQty()).isEqualTo(10);
    }

    @Test
    void testSearchAndFilter() {
        String unique = "Searchable_" + System.currentTimeMillis();
        productDAO.create(new Product(null, sellerId, categoryId, unique + " Keyboard", "Mechanical",
            new BigDecimal("2999.00"), 5, null, true, null));

        List<Product> results = productDAO.searchAndFilter(unique, categoryId, true, 10, 0);
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getName()).contains(unique);
    }

    @Test
    void testStockUpdate() {
        Product p = productDAO.create(new Product(null, sellerId, categoryId, "Stock Test", "Desc",
            new BigDecimal("500.00"), 20, null, true, null));

        boolean deducted = productDAO.updateStock(p.getId(), -5);
        assertThat(deducted).isTrue();

        Product refreshed = productDAO.findById(p.getId()).orElseThrow();
        assertThat(refreshed.getStockQty()).isEqualTo(15);
    }
}
