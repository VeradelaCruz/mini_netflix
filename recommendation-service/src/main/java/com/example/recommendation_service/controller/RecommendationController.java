package com.example.recommendation_service.controller;

import com.example.recommendation_service.models.Recommendation;
import com.example.recommendation_service.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recommendation")
public class RecommendationController {
    @Autowired
    private  RecommendationService recommendationService;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllRecommendations(){
        List<Recommendation> list=recommendationService.findAllRecommendations();
        return  ResponseEntity.ok(list);
    }
    @GetMapping("/userId/{userId}")
    public ResponseEntity<?> getByUserId(@PathVariable String userId){
       return ResponseEntity.ok(recommendationService.findByUserId(userId));
    }

    @PostMapping("/createRecommendation")
    public ResponseEntity<List<Recommendation>> createRecommendations() {
        List<Recommendation> recommendations = recommendationService.createRecommendation();
        return ResponseEntity.ok(recommendations);
    }

}
