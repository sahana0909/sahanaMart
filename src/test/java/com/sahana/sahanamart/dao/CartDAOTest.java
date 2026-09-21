package com.sahana.sahanamart.dao;

import com.sahana.sahanamart.dao.impl.CartDAOImpl;
import com.sahana.sahanamart.dao.impl.ProductDAOImpl;
import com.sahana.sahanamart.dao.impl.UserDAOImpl;
import com.sahana.sahanamart.model.CartItem;
import com.sahana.sahanamart.model.Product;
import com.sahana.sahanamart.model.Role;
import com.sahana.sahanamart.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CartDAOTest extends BaseDAOTest {

    private CartDAO cartDAO;
    private Long buyerId;
    private Long productId;

    @BeforeEach
    void setUp() {
        cartDAO = new CartDAOImpl();
        UserDAO userDAO = new UserDAOImpl();
        ProductDAO productDAO = new ProductDAOImpl();

        User buyer = userDAO.findByEmail("buyer_cart_test@example.com")
            .orElseGet(() -> userDAO.create(new User(null, "buyer_cart_test@example.com", "hash", "Cart Buyer", Role.BUYER, "123", null)));
        buyerId = buyer.getId();

        User seller = userDAO.findByEmail("seller_cart_test@example.com")
            .orElseGet(() -> userDAO.create(new User(null, "seller_cart_test@example.com", "hash", "Cart Seller", Role.SELLER, "123", null)));

        Product p = productDAO.create(new Product(null, seller.getId(), 1L, "Cart Product", "Desc",
            new BigDecimal("150.00"), 50, null, true, null));
        productId = p.getId();

        cartDAO.clearCart(buyerId);
    }

    @Test
    void testAddAndRetrieveCartItem() {
        cartDAO.addItem(buyerId, productId, 2);

        List<CartItem> items = cartDAO.findByUserId(buyerId);
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getQuantity()).isEqualTo(2);
        assertThat(items.get(0).getProduct().getName()).isEqualTo("Cart Product");

        // Add again should increment quantity
        cartDAO.addItem(buyerId, productId, 3);
        List<CartItem> updated = cartDAO.findByUserId(buyerId);
        assertThat(updated.get(0).getQuantity()).isEqualTo(5);
    }

    @Test
    void testRemoveAndClearCart() {
        CartItem item = cartDAO.addItem(buyerId, productId, 1);
        assertThat(cartDAO.countItems(buyerId)).isEqualTo(1);

        cartDAO.removeItem(item.getId(), buyerId);
        assertThat(cartDAO.countItems(buyerId)).isEqualTo(0);
    }
}
