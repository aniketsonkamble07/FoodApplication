package com.foodordering.foodapp.service;

import com.foodordering.foodapp.model.FoodItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FoodItemService {
    // Basic CRUD operations
    FoodItem createItem(FoodItem item);
    FoodItem updateItem(FoodItem item);
    void deleteItem(Long id);
    Optional<FoodItem> getItemById(Long id);

    // Query methods
    Page<FoodItem> getAllItems(Pageable pageable);
    Optional<FoodItem> getItemByName(String name);
    List<FoodItem> getItemsByCategory(String category);
    Page<FoodItem> getItemsByCategory(String category, Pageable pageable);
    List<FoodItem> getAvailableItems();
    Page<FoodItem> getItemsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    // Search functionality
    List<FoodItem> searchItems(String query);
    Page<FoodItem> searchItems(String query, Pageable pageable);

    // Business operations
    FoodItem toggleAvailability(Long id);
    boolean itemExists(String name);

    // Image handling
    void updateItemImage(Long itemId, byte[] imageData);

    // Categories
    Set<String> getAllCategories();
}