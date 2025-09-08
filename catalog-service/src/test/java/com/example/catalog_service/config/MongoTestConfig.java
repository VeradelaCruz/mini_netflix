package com.example.catalog_service.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
//para conectar solo a la base de test y
// evitar cualquier interacción accidental con la real.
@TestConfiguration
public class MongoTestConfig {

    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDatabaseFactory) {
        // Solo usar la base de test
        return new MongoTemplate(mongoDatabaseFactory);
    }

}
