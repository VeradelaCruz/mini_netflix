package com.example.rating_service.exception;

public class MovieNotFoundById extends RuntimeException {
    public MovieNotFoundById(String movieId) {
        super("Movie with id: " + movieId+ " not found.");
    }
}
