package com.example.recommendation_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

public class GlobalHandlerException {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> UserNotFoundExceptionHandler(UserNotFoundException ex) {
        Map<String, String> error = Map.of("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<Map<String, String>> MovieNotFoundExceptionHandler(MovieNotFoundException ex) {
        Map<String, String> error = Map.of("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
