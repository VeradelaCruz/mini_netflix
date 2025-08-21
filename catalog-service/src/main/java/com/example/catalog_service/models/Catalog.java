package com.example.catalog_service.models;

import com.example.catalog_service.enums.Genre;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "catalog")
public class Catalog {
    @Id
    private String movieId;
    @Size(max = 30, message = "The title must have up to 30 characters")
    private  String title;
    @NotNull(message = "Genre is required")
    private Genre genre;
    @NotNull
    private Integer releaseYear;
    @Size(max = 150, message = "The description must have up to 150 characters")
    private String description;
    @NotNull(message = "Rating is required")
    private Double ratingAverage;

    // --- Getters y Setters ---

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

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getRatingAverage() {
        return ratingAverage;
    }

    public void setRatingAverage(Double ratingAverage) {
        this.ratingAverage = ratingAverage;
    }
}
