package com.carldev.product_catalog_service.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {

        Map<String, String> erros = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach( error ->
                erros.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.badRequest().body(erros);
    }

    @ExceptionHandler(SkuAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleIfSkuAlreadyExists(SkuAlreadyExistsException ex) {

        Map<String, String> erros = new HashMap<>();
        erros.put("message: ", ex.getMessage());

        return ResponseEntity.badRequest().body(erros);
    }

    @ExceptionHandler(ProductIdNotExistsException.class)
    public ResponseEntity<Map<String, String>> handleIfIdNotExists(ProductIdNotExistsException ex) {

        Map<String, String> erros = new HashMap<>();
        erros.put("message: ", ex.getMessage());

        return ResponseEntity.badRequest().body(erros);
    }

    @ExceptionHandler(InvalidUpdateRequestException.class)
    public ResponseEntity<Map<String, String>> handleIfInvalidUpdateExists(InvalidUpdateRequestException ex) {

        Map<String, String> erros = new HashMap<>();
        erros.put("message: ", ex.getMessage());

        return ResponseEntity.badRequest().body(erros);
    }

    @ExceptionHandler(SlugAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleIfSlugAlreadyExists(SlugAlreadyExistsException ex) {
        Map<String, String> erros = new HashMap<>();
        erros.put("message", ex.getMessage());

        return ResponseEntity.badRequest().body(erros);
    }

}
