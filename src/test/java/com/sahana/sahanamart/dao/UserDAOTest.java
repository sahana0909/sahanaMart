package com.sahana.sahanamart.dao;

import com.sahana.sahanamart.dao.impl.UserDAOImpl;
import com.sahana.sahanamart.model.Role;
import com.sahana.sahanamart.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class UserDAOTest extends BaseDAOTest {

    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAOImpl();
    }

    @Test
    void testCreateAndFindUser() {
        String email = "testuser_" + System.currentTimeMillis() + "@example.com";
        User user = new User(null, email, "hashedPassword123", "Test User", Role.BUYER, "1234567890", null);

        User created = userDAO.create(user);
        assertThat(created.getId()).isNotNull();

        Optional<User> foundById = userDAO.findById(created.getId());
        assertThat(foundById).isPresent();
        assertThat(foundById.get().getEmail()).isEqualTo(email);
        assertThat(foundById.get().getFullName()).isEqualTo("Test User");
        assertThat(foundById.get().getRole()).isEqualTo(Role.BUYER);

        Optional<User> foundByEmail = userDAO.findByEmail(email);
        assertThat(foundByEmail).isPresent();
        assertThat(foundByEmail.get().getId()).isEqualTo(created.getId());
    }

    @Test
    void testUpdateUser() {
        String email = "update_" + System.currentTimeMillis() + "@example.com";
        User user = userDAO.create(new User(null, email, "hashed", "Initial Name", Role.SELLER, "111", null));

        user.setFullName("Updated Name");
        user.setPhone("999");
        boolean updated = userDAO.update(user);

        assertThat(updated).isTrue();
        User refreshed = userDAO.findById(user.getId()).orElseThrow();
        assertThat(refreshed.getFullName()).isEqualTo("Updated Name");
        assertThat(refreshed.getPhone()).isEqualTo("999");
    }
}
