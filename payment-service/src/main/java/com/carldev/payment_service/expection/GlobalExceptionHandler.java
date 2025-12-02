package com.carldev.payment_service.expection;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException e) {

        Map<String, String> erro  = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach(erros ->
                        erro.put(erros.getField(), erros.getDefaultMessage())
                );

        return ResponseEntity.badRequest().body(erro);
    }
}
