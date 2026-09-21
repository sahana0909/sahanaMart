package com.sahana.sahanamart.dto;

import com.sahana.sahanamart.model.Role;
import com.sahana.sahanamart.model.User;

import java.io.Serializable;
import java.sql.Timestamp;

public class UserResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String email;
    private String fullName;
    private Role role;
    private String phone;
    private Timestamp createdAt;

    public UserResponseDTO() {}

    public UserResponseDTO(Long id, String email, String fullName, Role role, String phone, Timestamp createdAt) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.phone = phone;
        this.createdAt = createdAt;
    }

    public static UserResponseDTO fromUser(User user) {
        if (user == null) {
            return null;
        }
        return new UserResponseDTO(
            user.getId(),
            user.getEmail(),
            user.getFullName(),
            user.getRole(),
            user.getPhone(),
            user.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
