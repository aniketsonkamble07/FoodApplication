package com.foodordering.foodapp.repository;

import com.foodordering.foodapp.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // You can add custom queries here if needed
}
