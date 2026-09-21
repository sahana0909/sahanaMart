package com.sahana.sahanamart.dao;

import com.sahana.sahanamart.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDAO {
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    User create(User user);
    boolean update(User user);
    boolean delete(Long id);
    List<User> findAll();
    long count();
}
