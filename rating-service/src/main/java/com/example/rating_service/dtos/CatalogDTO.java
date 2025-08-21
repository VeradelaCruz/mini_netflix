package com.example.rating_service.dtos;

import lombok.Data;

@Data
public class CatalogDTO {
    private String movieId;
    private  String title;
    private String description;
    private Double ratingAverage;


    public String getMovieId() {
        return movieId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Double getRatingAverage() {
        return ratingAverage;
    }


    public void setRatingAverage(Double ratingAverage) {
        this.ratingAverage = ratingAverage;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }


}
