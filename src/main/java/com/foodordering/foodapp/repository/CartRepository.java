package com.foodordering.foodapp.repository;

import com.foodordering.foodapp.model.Cart;
import com.foodordering.foodapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}
