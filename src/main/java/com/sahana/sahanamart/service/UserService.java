package com.sahana.sahanamart.service;

import com.sahana.sahanamart.dto.UserLoginDTO;
import com.sahana.sahanamart.dto.UserRegisterDTO;
import com.sahana.sahanamart.dto.UserResponseDTO;
import com.sahana.sahanamart.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserResponseDTO register(UserRegisterDTO registerDTO);
    User login(UserLoginDTO loginDTO);
    Optional<UserResponseDTO> getUserById(Long id);
    List<UserResponseDTO> getAllUsers();
    long getTotalUserCount();
}
