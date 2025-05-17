package com.foodordering.foodapp.exception;


public class FoodItemNotFoundException extends RuntimeException {
    public FoodItemNotFoundException(String message) {
        super(message);
    }

    public FoodItemNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}