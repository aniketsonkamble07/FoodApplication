package com.foodordering.foodapp.service;

import com.foodordering.foodapp.model.*;
import com.foodordering.foodapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;

    /**
     * Get existing cart or create new one for given username.
     *
     * @param username user identifier
     * @return cart instance
     */
    @Transactional
    public Cart getCartByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .totalPrice(BigDecimal.ZERO)
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    /**
     * Add or update an item quantity in user's cart.
     *
     * @param username   user identifier
     * @param foodItemId ID of the food item
     * @param quantity   amount to add
     * @return updated cart
     */
    @Transactional
    public Cart addItemToCart(String username, Long foodItemId, int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }

        Cart cart = getCartByUser(username);
        FoodItem foodItem = foodRepository.findById(foodItemId)
                .orElseThrow(() -> new RuntimeException("Food item not found: " + foodItemId));

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getFoodItem().getId().equals(foodItemId))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .foodItem(foodItem)
                    .quantity(quantity)
                    .build();
            cart.getItems().add(newItem);
            cartItemRepository.save(newItem);
        }

        updateCartTotalPrice(cart);
        return cartRepository.save(cart);
    }

    /**
     * Update the quantity of a cart item.
     *
     * @param username   user identifier
     * @param foodItemId food item ID
     * @param quantity   new quantity (if 0 or less, remove item)
     * @return updated cart
     */
    @Transactional
    public Cart updateItemQuantity(String username, Long foodItemId, int quantity) {
        Cart cart = getCartByUser(username);

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getFoodItem().getId().equals(foodItemId))
                .findFirst();

        if (existingItemOpt.isEmpty()) {
            throw new RuntimeException("Item not found in cart: " + foodItemId);
        }

        CartItem item = existingItemOpt.get();

        if (quantity < 1) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }

        updateCartTotalPrice(cart);
        return cartRepository.save(cart);
    }

    /**
     * Remove a specific item from the cart.
     *
     * @param username   user identifier
     * @param foodItemId food item ID to remove
     * @return updated cart
     */
    @Transactional
    public Cart removeItemFromCart(String username, Long foodItemId) {
        Cart cart = getCartByUser(username);

        cart.getItems().removeIf(item -> {
            if (item.getFoodItem().getId().equals(foodItemId)) {
                cartItemRepository.delete(item);
                return true;
            }
            return false;
        });

        updateCartTotalPrice(cart);
        return cartRepository.save(cart);
    }

    /**
     * Clear all items from the user's cart.
     *
     * @param username user identifier
     */
    @Transactional
    public void clearCart(String username) {
        Cart cart = getCartByUser(username);
        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    /**
     * Helper method to recalculate the total price of the cart.
     *
     * @param cart the cart to update
     */
    private void updateCartTotalPrice(Cart cart) {
        BigDecimal total = cart.getItems().stream()
                .map(CartItem::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(total);
    }
}
