package com.example.rating_service.dtos;

import lombok.Data;

@Data
public class RatingScoreDTO {
    private String movieId;
    private Double ratingAverage;

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public Double getRatingAverage() {
        return ratingAverage;
    }

    public void setRatingAverage(Double ratingAverage) {
        this.ratingAverage = ratingAverage;
    }

}