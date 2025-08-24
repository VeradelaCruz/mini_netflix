package com.example.catalog_service.dtos;

import com.example.catalog_service.enums.Genre;
import com.example.catalog_service.models.Catalog;
import lombok.Data;

@Data
public class CatalogDTO {
    private String movieId;
    private  String title;
    private Genre genre;

    public CatalogDTO(Catalog catalog) {
        this.movieId = catalog.getMovieId();
        this.title = catalog.getTitle();
        this.genre = catalog.getGenre();
    }


    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }
}
