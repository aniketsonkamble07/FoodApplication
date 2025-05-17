package com.foodordering.foodapp.repository;

import com.foodordering.foodapp.model.FoodItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FoodRepository extends JpaRepository<FoodItem , Long> {
    boolean existsByName(String name);
    Optional<FoodItem> findByName(String name);
    List<FoodItem> findByCategory(String category);
    Page<FoodItem> findByCategory(String category, Pageable pageable);
    List<FoodItem> findByAvailableTrue();
    Page<FoodItem> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    @Query("SELECT DISTINCT f.category FROM FoodItem f")
    Set<String> findAllCategories();

    List<FoodItem> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);

    Page<FoodItem> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String name, String description, Pageable pageable);
}
