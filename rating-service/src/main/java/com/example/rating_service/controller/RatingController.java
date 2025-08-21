package com.example.rating_service.controller;

import com.example.rating_service.dtos.RatingAverageDTO;
import com.example.rating_service.dtos.RatingDTO;
import com.example.rating_service.dtos.RatingUserDTO;
import com.example.rating_service.models.Rating;
import com.example.rating_service.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rating")
public class RatingController {
    @Autowired
    private RatingService ratingService;

    @PostMapping("/addRating")
    public ResponseEntity<List<Rating>> addRating(@Valid @RequestBody List<Rating> list){
        List<Rating> ratingList =ratingService.createRating(list);
        return ResponseEntity.ok(ratingList);
    }

    @PostMapping("/addOneRating")
    public ResponseEntity<Rating> addOneRating(@RequestBody RatingUserDTO dto) {
        Rating saved = ratingService.createOneRating(dto);
        return ResponseEntity.ok(saved);
    }


    @GetMapping("/getAll")
    public List<Rating> getAll(){
        return ratingService.findAllRating();
    }

    @GetMapping("/{id}")
    public Rating getById(@PathVariable String id){
        return ratingService.findById(id);
    }

    @GetMapping("/byMovie/{movieId}")
    public ResponseEntity<?> getByMovieId(@PathVariable String movieId){
        List<Rating> found= ratingService.findAllByMovieId(movieId);
        return ResponseEntity.ok(found);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateRating(@PathVariable String id,
                                          @Valid @RequestBody RatingDTO ratingDTO){
        RatingDTO updatedRating=ratingService.changeRating(id, ratingDTO);
        return ResponseEntity.ok(updatedRating);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteRating(@PathVariable String id){
        ratingService.removeRating(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/getAverage/{movieId}")
    public ResponseEntity<?> getAverage(@PathVariable String movieId){
        RatingAverageDTO ratingAverageDTO= ratingService.calculateAverageScore(movieId);
        return ResponseEntity.ok(ratingAverageDTO);
    }
}
