package com.example.recommendation_service.models;

import com.example.recommendation_service.dtos.CatalogDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "recommendation")
public class Recommendation {
    private String userId;
    private List<CatalogDTO> recommendedMovies;
    @NotNull
    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate generatedAt;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<CatalogDTO> getRecommendedMovies() {
        return recommendedMovies;
    }

    public void setRecommendedMovies(List<CatalogDTO> recommendedMovies) {
        this.recommendedMovies = recommendedMovies;
    }

    public LocalDate getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDate generatedAt) {
        this.generatedAt = generatedAt;
    }
}