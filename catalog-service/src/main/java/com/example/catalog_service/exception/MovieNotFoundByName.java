package com.example.catalog_service.exception;

public class MovieNotFoundByName extends RuntimeException {
    public MovieNotFoundByName(String title)
    {
        super("Movie with title: "+ title + " could not be found.");
    }
}
