package com.example.catalog_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

public class GlobalHandlerException {
@ExceptionHandler(MovieNotFound.class)
    public ResponseEntity<Map<String,String>> MovieNotFoundHandlerException( MovieNotFound ex){
    Map<String, String> error = Map.of("message", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
}
@ExceptionHandler(MovieNotFoundByName.class)
    public ResponseEntity<Map<String, String>> MovieNotFoundByTitleException(MovieNotFoundByName ex){
    Map<String, String> error= Map.of("message", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
}
}
