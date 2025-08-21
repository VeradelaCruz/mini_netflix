package com.example.rating_service.models;

import com.example.rating_service.enums.Score;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
@Document(collection = "rating")
public class Rating {
    @Id
    private String id;
    @NotEmpty(message = "userId must not be null")
    private String userId;

    @NotEmpty(message = "movieId must not be null")
    private String movieId;

    @NotNull(message = "Score must not be null")
    private Score score;

    @Size(max = 300, message = "The comment must have up to 300 characters")
    private String comment;

    @CreatedDate
    private LocalDate createdAt;

    public Score getScore() {
        return this.score;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setScore(Score score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}

