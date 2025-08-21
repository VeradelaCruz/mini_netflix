package com.example.catalog_service.dtos;

import com.example.catalog_service.enums.Genre;
import lombok.Data;

@Data
public class CatalogUpdateDto {
    private String title;
    private String genre;
    private String description;
    private Double ratingAverage;
    private Integer releaseYear;

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }
    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}

