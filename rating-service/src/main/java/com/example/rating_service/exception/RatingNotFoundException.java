package com.example.rating_service.exception;

public class RatingNotFoundException extends RuntimeException {
    public RatingNotFoundException(String id) {
        super("Rating with id: "+ id + " not found.");
    }
}
