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
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

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

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalStateException(IllegalStateException ex) {
        log.error("Illegal state error: {}", ex.getMessage());

        Map<String, Object> response = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Illegal State Error",
                ex.getMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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

        HttpStatus status = HttpStatus.UNAUTHORIZED;

        Map<String, Object> response = createErrorResponse(
                status,
                "Insufficient Authentication",
                ex.getMessage()
        );

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        Map<String, Object> response = createErrorResponse(
                status,
                "Server Error",
                "An unexpected error occurred"
        );

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<Map<String, Object>> handleLockedException(LockedException ex) {
        log.error("Account locked error: {}", ex.getMessage());

        HttpStatus status = HttpStatus.LOCKED;

        Map<String, Object> response = createErrorResponse(
                status,
                "Account Locked",
                ex.getMessage()
        );

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> handleNoSuchElementException(NoSuchElementException ex) {
        log.error("The element does not exist: {}", ex.getMessage());

        HttpStatus status = HttpStatus.NOT_FOUND;

        Map<String, Object> response = createErrorResponse(
                status,
                "The element was not found.",
                ex.getMessage()
        );

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        log.error("Authorization denied error: {}", ex.getMessage());

        HttpStatus status = HttpStatus.FORBIDDEN;

        Map<String, Object> response = createErrorResponse(
                status,
                "Authorization Denied",
                ex.getMessage()
        );

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        // You can inspect the cause of the exception to provide a more specific message
        // For unique constraint violations, the cause often contains details.

        HttpStatus status = HttpStatus.CONFLICT;

        if (ex.getCause() != null && ex.getCause().getMessage() != null) {
            String causeMessage = ex.getCause().getMessage();
            if (causeMessage.contains("duplicate key value violates unique constraint") ||
                    causeMessage.contains("Unique index or primary key violation")) {

                if (causeMessage.contains("name")) {
                    Map<String, Object> duplicateResponse = createErrorResponse(
                            status,
                            "Cannot save record with duplicate name. Please use a unique value.",
                            "Error violating unique constraint."
                    );
                    return new ResponseEntity<>(duplicateResponse, status);
                }

                Map<String, Object> duplicateResponse = createErrorResponse(
                        status,
                        "The record you are trying to save already exists (e.g., duplicate name). Please use a unique value.",
                        "Error violating unique constraint."
                );
                return new ResponseEntity<>(duplicateResponse, status);
            }
        }
        Map<String, Object> defaultResponse = createErrorResponse(
                status,
                "A data integrity violation occurred.",
                "Error violating unique constraint."
        );
        return new ResponseEntity<>(defaultResponse, status); // 409 Conflict is appropriate for unique constraint violations
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
