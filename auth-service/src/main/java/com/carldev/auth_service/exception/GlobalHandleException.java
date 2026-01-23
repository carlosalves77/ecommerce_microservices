package com.carldev.auth_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalHandleException {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorValidationExceptionDTO>> handleValidationException(MethodArgumentNotValidException ex) {

        var errorResponse = ex.getBindingResult().getFieldErrors().stream().map(
                erros -> new ErrorValidationExceptionDTO(
                        HttpStatus.UNAUTHORIZED.value(),
                        erros.getField(),
                        erros.getDefaultMessage(),
                        System.currentTimeMillis()
                )
        ).toList();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(UserExistEmailException.class)
    public ResponseEntity<ErrorResponseDTO> handleExistsAlreadyEmail(UserExistEmailException ex) {

        ErrorResponseDTO responseDTO = new ErrorResponseDTO(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                System.currentTimeMillis()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDTO);
    }

    @ExceptionHandler(HandleIfInvalidTokenOrExpireException.class)
    public ResponseEntity<ErrorResponseDTO> HandleIfInvalidTokenOrExpireException(
            HandleIfInvalidTokenOrExpireException ex) {

        ErrorResponseDTO responseDTO  = new ErrorResponseDTO(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                System.currentTimeMillis()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDTO);
    }

    @ExceptionHandler(HandleIfUserNotExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleIfUsernameNotExistsException(HandleIfUserNotExistsException ex) {

      ErrorResponseDTO responseDTO = new ErrorResponseDTO(
              HttpStatus.UNAUTHORIZED.value(),
              ex.getMessage(),
              System.currentTimeMillis()
      );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDTO);
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

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDTO);
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

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDTO);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleIfCredentialIsNotValidException(
            BadCredentialsException ex) {
        ErrorResponseDTO responseDTO = new ErrorResponseDTO(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                System.currentTimeMillis()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDTO);
    }
}
