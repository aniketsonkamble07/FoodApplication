package com.foodordering.foodapp.service;

import com.foodordering.foodapp.model.User;

import java.util.List;


public interface UserService {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> getUserRole(String role);
    User registerUser(User user);
    User loginUser(User user);

}

