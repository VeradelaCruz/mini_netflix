package com.example.user_service.client;

import com.example.user_service.dtos.RatingUserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "rating-service", url = "http://localhost:8082/rating")
public interface RatingClient {

    @PostMapping("/addOneRating")
    void addOneRating(@RequestBody RatingUserDTO ratingUserDTO);
}
