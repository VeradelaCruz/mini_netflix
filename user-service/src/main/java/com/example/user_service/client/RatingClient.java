package com.example.user_service.client;


import com.example.user_service.dtos.RatingUserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "rating-service",
        url = "http://localhost:8082/rating")

public interface RatingClient {
    @PostMapping("/addAndCalculateAverage")
    void addAndCalculateAverage(@RequestBody RatingUserDTO ratingUserDTO);

    @GetMapping("/byMovie/{movieId}")
    List<RatingUserDTO> getRatingsByMovie(@PathVariable String movieId);
}