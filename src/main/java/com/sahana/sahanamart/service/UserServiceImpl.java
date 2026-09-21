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
import com.sahana.sahanamart.util.ValidationUtil;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = Logger.getLogger(UserServiceImpl.class.getName());
    private final UserDAO userDAO;

    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public UserResponseDTO register(UserRegisterDTO dto) {
        if (dto == null) {
            throw new ValidationException("Registration data cannot be null");
        }
        // Input validation
        ValidationUtil.validateRegistration(
            dto.getEmail(),
            dto.getPassword(),
            dto.getConfirmPassword(),
            dto.getFullName(),
            dto.getRole()
        );

        // Check if email already exists
        if (userDAO.findByEmail(dto.getEmail()).isPresent()) {
            throw new ValidationException("email", "An account with this email already exists");
        }

        Role role = Role.fromString(dto.getRole());
        if (role == Role.ADMIN) {
            throw new ValidationException("role", "Cannot self-register as ADMIN role");
        }

        String hashedPassword = PasswordUtil.hash(dto.getPassword());
        User user = new User();
        user.setEmail(dto.getEmail().trim().toLowerCase());
        user.setPasswordHash(hashedPassword);
        user.setFullName(dto.getFullName().trim());
        user.setRole(role);
        user.setPhone(dto.getPhone());

        User created = userDAO.create(user);
        LOGGER.info("Successfully registered user: " + created.getEmail() + " with role " + created.getRole());
        return UserResponseDTO.fromUser(created);
    }

    @Override
    public User login(UserLoginDTO dto) {
        if (dto == null || dto.getEmail() == null || dto.getPassword() == null) {
            throw new AuthenticationException("Email and password are required");
        }

        User user = userDAO.findByEmail(dto.getEmail().trim())
            .orElseThrow(() -> new AuthenticationException("Invalid email or password"));

        if (!PasswordUtil.verify(dto.getPassword(), user.getPasswordHash())) {
            LOGGER.warning("Failed login attempt for email: " + dto.getEmail());
            throw new AuthenticationException("Invalid email or password");
        }

        LOGGER.info("Successful login for user: " + user.getEmail() + " (role=" + user.getRole() + ")");
        return user;
    }

    @Override
    public Optional<UserResponseDTO> getUserById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return userDAO.findById(id).map(UserResponseDTO::fromUser);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userDAO.findAll().stream()
            .map(UserResponseDTO::fromUser)
            .collect(Collectors.toList());
    }

    @Override
    public long getTotalUserCount() {
        return userDAO.count();
    }
}
