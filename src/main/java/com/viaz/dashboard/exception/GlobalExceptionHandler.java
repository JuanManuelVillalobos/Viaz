package com.viaz.dashboard.exception;

import com.viaz.dashboard.exception.exceptions.ResourceNotFoundException;
import com.viaz.dashboard.exception.exceptions.TokenValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TokenValidationException.class)
    public ResponseEntity<Map<String, Object>> handleTokenValidationException(TokenValidationException ex) {
        log.error("Token validation error: {}", ex.getMessage());

        Map<String, Object> response = createErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Token Validation Error",
                ex.getMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentialsException(BadCredentialsException ex) {
        log.error("Authentication error: {}", ex.getMessage());

        Map<String, Object> response = createErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Authentication Error",
                ex.getMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());

        Map<String, Object> response = createErrorResponse(
                HttpStatus.NOT_FOUND,
                "Resource Not Found",
                ex.getMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientAuthenticationException(InsufficientAuthenticationException ex) {
        log.error("Insufficient authentication: {}", ex.getMessage());

        Map<String, Object> response = createErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Insufficient Authentication",
                ex.getMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);

        Map<String, Object> response = createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Server Error",
                "An unexpected error occurred"
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<Map<String, Object>> handleLockedException(LockedException ex) {
        log.error("Account locked error: {}", ex.getMessage());

        Map<String, Object> response = createErrorResponse(
                HttpStatus.LOCKED,
                "Account Locked",
                ex.getMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.LOCKED);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        // You can inspect the cause of the exception to provide a more specific message
        // For unique constraint violations, the cause often contains details.
        String errorMessage = "A data integrity violation occurred.";

        if (ex.getCause() != null && ex.getCause().getMessage() != null) {
            String causeMessage = ex.getCause().getMessage();
            if (causeMessage.contains("duplicate key value violates unique constraint") ||
                    causeMessage.contains("Unique index or primary key violation")) {

                if (causeMessage.contains("name")) {
                    Map<String, Object> duplicateResponse = createErrorResponse(
                            HttpStatus.CONFLICT,
                            "Cannot save record with duplicate name. Please use a unique value.",
                            "Error violating unique constraint."
                    );
                    return new ResponseEntity<>(duplicateResponse, HttpStatus.CONFLICT);
                }

                Map<String, Object> duplicateResponse = createErrorResponse(
                        HttpStatus.CONFLICT,
                        "The record you are trying to save already exists (e.g., duplicate name). Please use a unique value.",
                        "Error violating unique constraint."
                );
                return new ResponseEntity<>(duplicateResponse, HttpStatus.CONFLICT);
            }
        }
        Map<String, Object> defaultResponse = createErrorResponse(
                HttpStatus.CONFLICT,
                "A data integrity violation occurred.",
                "Error violating unique constraint."
        );
        return new ResponseEntity<>(defaultResponse, HttpStatus.CONFLICT); // 409 Conflict is appropriate for unique constraint violations
    }


    private Map<String, Object> createErrorResponse(HttpStatus status, String error, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", error);
        response.put("message", message);
        return response;
    }
}
