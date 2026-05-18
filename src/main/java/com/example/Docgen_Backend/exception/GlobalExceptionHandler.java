package com.example.Docgen_Backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 🔹 Common builder
    private Map<String, Object> buildError(String message, HttpStatus status, String path) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("timestamp", LocalDateTime.now());
        error.put("message", message);
        error.put("statusCode", status.value());
        error.put("path", path);
        return error;
    }

    // =========================
    // USER NOT FOUND
    // =========================
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound(UserNotFoundException ex, HttpServletRequest request) {

        return new ResponseEntity<>(
                buildError(ex.getMessage(), HttpStatus.NOT_FOUND, request.getRequestURI()),
                HttpStatus.NOT_FOUND
        );
    }

    // =========================
    // DUPLICATE USER
    // =========================
    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<?> handleDuplicateUser(DuplicateUserException ex, HttpServletRequest request) {

        return new ResponseEntity<>(
                buildError(ex.getMessage(), HttpStatus.BAD_REQUEST, request.getRequestURI()),
                HttpStatus.BAD_REQUEST
        );
    }

    // =========================
    // BAD REQUEST (Illegal args)
    // =========================
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {

        return new ResponseEntity<>(
                buildError(ex.getMessage(), HttpStatus.BAD_REQUEST, request.getRequestURI()),
                HttpStatus.BAD_REQUEST
        );
    }

    // =========================
    // VALIDATION ERROR (@Valid)
    // =========================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {

        String errorMsg = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(err -> err.getField() + " : " + err.getDefaultMessage())
                .orElse("Validation failed");

        return new ResponseEntity<>(
                buildError(errorMsg, HttpStatus.BAD_REQUEST, request.getRequestURI()),
                HttpStatus.BAD_REQUEST
        );
    }

    // =========================
    // GENERIC ERROR
    // =========================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception ex, HttpServletRequest request) {

        return new ResponseEntity<>(
                buildError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}