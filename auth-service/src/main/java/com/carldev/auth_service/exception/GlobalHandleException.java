package com.carldev.auth_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalHandleException {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(fieldErrors ->
                        errors.put(fieldErrors.getField(), fieldErrors.getDefaultMessage())
                );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(UserExistEmailException.class)
    public ResponseEntity<ErrorResponseDTO> handleExistsAlreadyEmail(UserExistEmailException ex) {

        ErrorResponseDTO responseDTO = new ErrorResponseDTO(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(responseDTO);
    }

    @ExceptionHandler(HandleIfInvalidTokenOrExpireException.class)
    public ResponseEntity<ErrorResponseDTO> HandleIfInvalidTokenOrExpireException(
            HandleIfInvalidTokenOrExpireException ex) {

        ErrorResponseDTO responseDTO = new ErrorResponseDTO(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDTO);
    }

    @ExceptionHandler(HandleIfUserNotExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleIfUsernameNotExistsException(HandleIfUserNotExistsException ex) {

        ErrorResponseDTO responseDTO = new ErrorResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
    }

    @ExceptionHandler(UserIsAlreadyVerifiedException.class)
    public ResponseEntity<ErrorResponseDTO> handleIfUserIsAlreadyVerifiedException(
            UserIsAlreadyVerifiedException ex
    ) {

        ErrorResponseDTO responseDTO = new ErrorResponseDTO(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(responseDTO);
    }

    @ExceptionHandler(UserIsNotVerifyAccountException.class)
    public ResponseEntity<ErrorResponseDTO> handleIfUserIsNotVerifyAccountException(
            UserIsNotVerifyAccountException ex
    ) {

        ErrorResponseDTO responseDTO = new ErrorResponseDTO(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDTO);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleIfCredentialIsNotValidException(
            BadCredentialsException ex) {
        ErrorResponseDTO responseDTO = new ErrorResponseDTO(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDTO);
    }
}
