package com.example.catalog_service.exception;

public class MovieNotFound extends RuntimeException {
    public MovieNotFound(String id) {
        super("Movie with id: "+ id + " not found.");
    }
}
