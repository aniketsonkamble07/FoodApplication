package com.foodordering.foodapp.service;

import com.foodordering.foodapp.model.User;
import com.foodordering.foodapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User registerUser(User user) {
        if (existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        if (existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already in use");
        }
        if (user.getRole() == null) {
            user.setRole(User.Role.USER);  // Set default role
        }
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }

    @Override
    public List<User> getUserRole(String role) {
        try {
            User.Role userRole = User.Role.valueOf(role.toUpperCase());
            return userRepository.findByRole(userRole);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role: " + role);
        }
    }

    @Override
    public User loginUser(User user) {
        Optional<User> existingUser = userRepository.findByUsernameAndEmail(user.getUsername(), user.getEmail());

        if (existingUser.isPresent()) {
            User dbUser = existingUser.get();

            // Check role
            if (dbUser.getRole() == User.Role.USER) {
                // Check password
                if (passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
                    return dbUser;
                } else {
                    throw new RuntimeException("Incorrect password");
                }
            } else {
                throw new RuntimeException("Invalid user role");
            }
        }

        throw new RuntimeException("User not found with given username and email");
    }


}
