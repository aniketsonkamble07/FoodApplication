package com.foodordering.foodapp.service;

import com.foodordering.foodapp.exception.DuplicateFoodItemException;
import com.foodordering.foodapp.model.FoodItem;
import com.foodordering.foodapp.repository.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class FoodItemServiceImpl implements FoodItemService {

    private final FoodRepository foodRepository;

    @Autowired
    public FoodItemServiceImpl(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    @Override
    public FoodItem createItem(FoodItem item) {
        if (foodRepository.existsByName(item.getName())) {
            throw new DuplicateFoodItemException("Food item with name '" + item.getName() + "' already exists.");
        }
        return foodRepository.save(item);
    }

    @Override
    public FoodItem updateItem(FoodItem item) {
        return foodRepository.save(item);
    }

    @Override
    public void deleteItem(Long id) {
        foodRepository.deleteById(id);
    }

    @Override
    public Optional<FoodItem> getItemById(Long id) {
        return foodRepository.findById(id);
    }

    @Override
    public Page<FoodItem> getAllItems(Pageable pageable) {
        return foodRepository.findAll(pageable);
    }

    @Override
    public Optional<FoodItem> getItemByName(String name) {
        return foodRepository.findByName(name);
    }

    @Override
    public List<FoodItem> getItemsByCategory(String category) {
        return foodRepository.findByCategory(category);
    }

    @Override
    public Page<FoodItem> getItemsByCategory(String category, Pageable pageable) {
        return foodRepository.findByCategory(category, pageable);
    }

    @Override
    public List<FoodItem> getAvailableItems() {
        return foodRepository.findByAvailableTrue();
    }

    @Override
    public Page<FoodItem> getItemsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return foodRepository.findByPriceBetween(minPrice, maxPrice, pageable);
    }

    @Override
    public List<FoodItem> searchItems(String query) {
        return foodRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
    }

    @Override
    public Page<FoodItem> searchItems(String query, Pageable pageable) {
        return foodRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query, pageable);
    }

    @Override
    public FoodItem toggleAvailability(Long id) {
        FoodItem item = foodRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with id: " + id));
        item.setAvailable(!item.isAvailable());
        return foodRepository.save(item);
    }

    @Override
    public boolean itemExists(String name) {
        return foodRepository.existsByName(name);
    }

    @Override
    public void updateItemImage(Long itemId, byte[] imageData) {
        FoodItem item = foodRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with id: " + itemId));
        item.setImageData(imageData);
        foodRepository.save(item);
    }

    @Override
    public Set<String> getAllCategories() {
        return foodRepository.findAllCategories();
    }
}
