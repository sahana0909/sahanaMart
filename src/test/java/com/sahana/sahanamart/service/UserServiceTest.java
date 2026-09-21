package com.sahana.sahanamart.service;

import com.sahana.sahanamart.dao.UserDAO;
import com.sahana.sahanamart.dto.UserLoginDTO;
import com.sahana.sahanamart.dto.UserRegisterDTO;
import com.sahana.sahanamart.dto.UserResponseDTO;
import com.sahana.sahanamart.exception.AuthenticationException;
import com.sahana.sahanamart.exception.ValidationException;
import com.sahana.sahanamart.model.Role;
import com.sahana.sahanamart.model.User;
import com.sahana.sahanamart.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserDAO userDAO;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userDAO);
    }

    @Test
    void register_Success() {
        UserRegisterDTO dto = new UserRegisterDTO(
            "newuser@example.com", "Password@123", "Password@123", "New User", "BUYER", "9876543210"
        );

        when(userDAO.findByEmail("newuser@example.com")).thenReturn(Optional.empty());
        when(userDAO.create(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(10L);
            return u;
        });

        UserResponseDTO response = userService.register(dto);
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getEmail()).isEqualTo("newuser@example.com");
        verify(userDAO).create(any(User.class));
    }

    @Test
    void register_DuplicateEmail_ThrowsValidationException() {
        UserRegisterDTO dto = new UserRegisterDTO(
            "existing@example.com", "Password@123", "Password@123", "Name", "BUYER", null
        );

        when(userDAO.findByEmail("existing@example.com")).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> userService.register(dto))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("email");
    }

    @Test
    void login_Success() {
        String hashed = PasswordUtil.hash("Secret123");
        User user = new User(1L, "user@example.com", hashed, "Valid User", Role.BUYER, null, null);

        when(userDAO.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        User result = userService.login(new UserLoginDTO("user@example.com", "Secret123"));
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("user@example.com");
    }

    @Test
    void login_InvalidPassword_ThrowsAuthenticationException() {
        String hashed = PasswordUtil.hash("Secret123");
        User user = new User(1L, "user@example.com", hashed, "Valid User", Role.BUYER, null, null);

        when(userDAO.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.login(new UserLoginDTO("user@example.com", "WrongPassword")))
            .isInstanceOf(AuthenticationException.class)
            .hasMessageContaining("Invalid email or password");
    }
}
