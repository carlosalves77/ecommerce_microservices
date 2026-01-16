package com.carldev.shopping_cart_service.exception;

import com.carldev.shopping_cart_service.dto.response.ErrorExceptionResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorValidationExceptionDTO>> handleValidationException(MethodArgumentNotValidException ex) {

    var exceptionDTOS = ex.getBindingResult().getFieldErrors().stream().map(
            erros -> new ErrorValidationExceptionDTO(
                    HttpStatus.UNAUTHORIZED.value(),
                    erros.getField(),
                    erros.getDefaultMessage(),
                    System.currentTimeMillis()
            )
    ).toList();
        return ResponseEntity.ok().body(exceptionDTOS);

    }

    @ExceptionHandler(HandleSkuNotExistsException.class)
    public ResponseEntity<ErrorExceptionResponseDTO> handleIfSkuExistsException(HandleSkuNotExistsException ex) {

        ErrorExceptionResponseDTO responseDTO = new ErrorExceptionResponseDTO(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                System.currentTimeMillis()
        );

        return ResponseEntity.ok().body(responseDTO);

    }

    @ExceptionHandler(HandleIfCartNotFoundException.class)
    public ResponseEntity<ErrorExceptionResponseDTO> handleIfCartNotFoundException(HandleIfCartNotFoundException ex) {

        ErrorExceptionResponseDTO responseDTO = new ErrorExceptionResponseDTO(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                System.currentTimeMillis()
        );
        return ResponseEntity.ok().body(responseDTO);

    }

    @ExceptionHandler(HandleQuantityNotValidException.class)
    public ResponseEntity<ErrorExceptionResponseDTO> handleIfQuantityNotValidException
            (HandleQuantityNotValidException ex) {

        ErrorExceptionResponseDTO responseDTO = new ErrorExceptionResponseDTO(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                System.currentTimeMillis()
        );
        return ResponseEntity.ok().body(responseDTO);

    }

    @ExceptionHandler(HandleIfCartIsEmptyException.class)
    public ResponseEntity<ErrorExceptionResponseDTO> handleIfCartIsEmptyException(HandleIfCartIsEmptyException ex) {

        ErrorExceptionResponseDTO responseDTO = new ErrorExceptionResponseDTO(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                System.currentTimeMillis()
        );
        return ResponseEntity.ok().body(responseDTO);
    }

}
