package com.foodordering.foodapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_item_id", nullable = false)
    @NotNull
    private FoodItem foodItem;

    @Min(1)
    @Builder.Default
    private int quantity = 1;

    @Digits(integer = 6, fraction = 2)
    private BigDecimal subTotal;

    @Version
    private Integer version;

    @PrePersist
    @PreUpdate
    public void calculateSubTotal() {
        if (foodItem != null && quantity > 0) {
            subTotal = foodItem.getPrice().multiply(BigDecimal.valueOf(quantity));
        } else {
            subTotal = BigDecimal.ZERO;
        }
    }
}
