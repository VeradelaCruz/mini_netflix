package com.example.recommendation_service.exception;

public class MovieNotFoundException extends RuntimeException {
    public MovieNotFoundException(String movieId)
    {
        super("Movie with id: "+ movieId +" not found.");
    }

}
