package com.example.catalog_service.repository;

import com.example.catalog_service.models.Catalog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CatalogRepository extends MongoRepository<Catalog,String > {
    Optional<Catalog> findByTitle(String title);
}
