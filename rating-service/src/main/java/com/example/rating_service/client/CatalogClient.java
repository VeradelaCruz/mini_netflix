package com.example.rating_service.client;

import com.example.rating_service.dtos.CatalogDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "catalog-service", url = "http://localhost:8081")
public interface CatalogClient {
    @GetMapping("/catalog/{movieId}")
    CatalogDTO getById(@PathVariable("movieId") String movieId);

}
