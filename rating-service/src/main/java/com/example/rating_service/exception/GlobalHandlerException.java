package com.example.rating_service.exception;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@Configuration
public class GlobalHandlerException {
    @ExceptionHandler(MovieNotFoundById.class)
    public ResponseEntity<Map<String,String>> MovieNotFoundByIdHandler(MovieNotFoundById ex){
        Map<String, String> error = Map.of("message" , ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(RatingNotFoundException.class)
    public ResponseEntity<Map<String,String>> RatingNotFoundHandler(RatingNotFoundException ex){
        Map<String, String> error = Map.of("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
