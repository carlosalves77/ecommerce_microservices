package com.carldev.auth_service.exception;

import com.carldev.auth_service.dto.response.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalHandleException {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {

        Map<String, String> erros = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                erros.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.badRequest().body(erros);
    }

    @ExceptionHandler(UserExistEmailException.class)
    public ResponseEntity<ErrorResponseDTO> handleExistsAlreadyEmail(UserExistEmailException ex) {

        ErrorResponseDTO responseDTO = new ErrorResponseDTO(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                System.currentTimeMillis()
        );
        return ResponseEntity.badRequest().body(responseDTO);
    }

    @ExceptionHandler(HandleIfInvalidTokenOrExpireException.class)
    public ResponseEntity<ErrorResponseDTO> HandleIfInvalidTokenOrExpireException(
            HandleIfInvalidTokenOrExpireException ex) {

        ErrorResponseDTO responseDTO  = new ErrorResponseDTO(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                System.currentTimeMillis()
        );

        return ResponseEntity.badRequest().body(responseDTO);
    }

    @ExceptionHandler(HandleIfUserNotExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleIfUsernameNotExistsException(HandleIfUserNotExistsException ex) {

      ErrorResponseDTO responseDTO = new ErrorResponseDTO(
              HttpStatus.UNAUTHORIZED.value(),
              ex.getMessage(),
              System.currentTimeMillis()
      );

        return ResponseEntity.badRequest().body(responseDTO);
    }

    @ExceptionHandler(UserIsAlreadyVerifiedException.class)
    public ResponseEntity<ErrorResponseDTO> handleIfUserIsAlreadyVerifiedException(
            UserIsAlreadyVerifiedException ex
    ) {

        ErrorResponseDTO responseDTO = new ErrorResponseDTO(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                System.currentTimeMillis()
        );

        return ResponseEntity.badRequest().body(responseDTO);
    }

    @ExceptionHandler(UserIsNotVerifyAccountException.class)
    public ResponseEntity<ErrorResponseDTO> handleIfUserIsNotVerifyAccountException(
            UserIsNotVerifyAccountException ex
    ) {

        ErrorResponseDTO responseDTO = new ErrorResponseDTO(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                System.currentTimeMillis()
        );

        return ResponseEntity.badRequest().body(responseDTO);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleIfCredentialIsNotValidException(
            BadCredentialsException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                System.currentTimeMillis()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
}
