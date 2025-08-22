package com.example.user_service.client;

import com.example.user_service.dtos.RatingScoreDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "catalog-service", url = "http://localhost:8081/catalog")
public interface CatalogClient {

    @PutMapping("/updateScore")
    void updateScore(@RequestBody RatingScoreDTO ratingScoreDTO);

}