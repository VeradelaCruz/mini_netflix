package com.example.rating_service.repository;

import com.example.rating_service.models.Rating;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends MongoRepository<Rating,String > {
    List<Rating> findByMovieId(String movieId);

    Optional<Rating> findByUserId(String movieId);
}
