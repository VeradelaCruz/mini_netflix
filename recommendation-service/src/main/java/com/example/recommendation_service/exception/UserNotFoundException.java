package com.example.recommendation_service.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String userId) {
        super("User with name: "+ userId + " not found.");
    }
}