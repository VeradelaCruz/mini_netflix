package com.example.recommendation_service.service;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

@TestConfiguration
public class MongoTestConfig {
    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDatabaseFactory) {
        // Solo usar la base de test
        return new MongoTemplate(mongoDatabaseFactory);
    }
}
