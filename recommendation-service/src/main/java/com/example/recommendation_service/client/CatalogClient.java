package com.example.recommendation_service.client;

import com.example.recommendation_service.dtos.CatalogDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "catalog-service", url = "http://localhost:8081/catalog")
public interface CatalogClient {
    @GetMapping("/getAll")
    List<CatalogDTO> getAll();
}
