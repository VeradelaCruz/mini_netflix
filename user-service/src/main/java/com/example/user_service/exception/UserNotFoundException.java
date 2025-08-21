package com.example.user_service.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String userName) {
        super("User with name: "+ userName + " not found.");
    }
}
