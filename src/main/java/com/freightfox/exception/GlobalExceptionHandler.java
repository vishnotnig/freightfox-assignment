package com.freightfox.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Validation errors (@Valid failures) -> 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        e -> e.getField(),
                        e -> e.getDefaultMessage(),
                        (a, b) -> a // keep first message on duplicate field
                ));
        return ResponseEntity.badRequest()
                .body(Map.of("status", "error", "errors", fieldErrors));
    }

    // Business logic errors (infeasible, no data, lane not found) -> 422
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleInfeasible(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of("status", "error", "message", ex.getMessage()));
    }

    // Bad references (lane not found in input) -> 400
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException ex) {
        return ResponseEntity.badRequest()
                .body(Map.of("status", "error", "message", ex.getMessage()));
    }

    // Catch-all -> 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("status", "error", "message", "An unexpected error occurred."));
    }
}
