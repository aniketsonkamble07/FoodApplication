package com.foodordering.foodapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Base64;

@Entity
@Table(name = "food_items", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be less than 100 characters")
    private String name;

    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 5, fraction = 2, message = "Price format invalid")
    private BigDecimal price;

    @Lob // optional, only if you're using large image files
    private byte[] imageData;

    @NotBlank(message = "Category is required")
    private String category;

    private boolean available;

    @Transient
    public String getImageBase64() {
        return imageData != null ? Base64.getEncoder().encodeToString(imageData) : null;
    }
}
