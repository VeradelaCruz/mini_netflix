package com.example.recommendation_service.repository;

import com.example.recommendation_service.models.Recommendation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RecommendationRepository extends MongoRepository<Recommendation,String> {
    Optional<Recommendation> findByUserId(String userId);
}
