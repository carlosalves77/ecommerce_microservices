package com.carldev.shopping_cart_service.exception;

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

        ex.getBindingResult().getFieldErrors().forEach(erro ->
                erros.put(erro.getField(), erro.getDefaultMessage())
        );

        return ResponseEntity.badRequest().body(erros);

    }

    @ExceptionHandler(HandleSkuNotExistsException.class)
    public ResponseEntity<Map<String, String>> handleIfSkuExistsException(HandleSkuNotExistsException ex) {

        Map<String, String> erros = new HashMap<>();
        erros.put("Message: ", ex.getMessage());

        return ResponseEntity.badRequest().body(erros);

    }

    @ExceptionHandler(HandleIfCartNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleIfCartNotFoundException(HandleIfCartNotFoundException ex) {

        Map<String, String> erro = new HashMap<>();
        erro.put("Message: ", ex.getMessage());

        return ResponseEntity.badRequest().body(erro);
    }

    @ExceptionHandler(HandleQuantityNotValidException.class)
    public ResponseEntity<Map<String, String>> handleIfQuantityNotValidException
            (HandleQuantityNotValidException ex) {

        Map<String, String> erro = new HashMap<>();

        erro.put("Message: ", ex.getMessage());

        return ResponseEntity.badRequest().body(erro);
    }

}
