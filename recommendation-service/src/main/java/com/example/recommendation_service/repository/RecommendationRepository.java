package com.example.recommendation_service.repository;

import com.example.recommendation_service.models.Recommendation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RecommendationRepository extends MongoRepository<String, Recommendation> {
}
