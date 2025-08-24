package com.example.catalog_service.exception;

import com.example.catalog_service.enums.Genre;

public class GenreNotFoundException extends RuntimeException {
    public GenreNotFoundException(Genre genre) {
        super("Genre "+ genre + " not found.");
    }
}
