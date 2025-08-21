package com.example.recommendation_service.models;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "recommendation")
public class Recommendation {
    private String userId;
    private List<String> recommendedMovies;
    @NotNull
    @CreatedDate
    private LocalDate generatedAt;

}