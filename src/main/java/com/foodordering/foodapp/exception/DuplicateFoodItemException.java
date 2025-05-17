package com.foodordering.foodapp.exception;



public class DuplicateFoodItemException extends RuntimeException {
    public DuplicateFoodItemException(String message) {
        super(message);
    }

    public DuplicateFoodItemException(String message, Throwable cause) {
        super(message, cause);
    }
}