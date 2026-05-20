package com.example.Docgen_Backend.exception;

import com.example.Docgen_Backend.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 🔹 Common builder
    private ErrorResponse buildError(String message, HttpStatus status, String path) {
        return ErrorResponse.builder()
                .success(false)
                .message(message)
                .statusCode(status.value())
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // =========================
    // USER NOT FOUND
    // =========================
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            UserNotFoundException ex,
            HttpServletRequest request) {

        log.error("User not found: {}", ex.getMessage());

        return new ResponseEntity<>(
                buildError(ex.getMessage(), HttpStatus.NOT_FOUND, request.getRequestURI()),
                HttpStatus.NOT_FOUND
        );
    }

    // =========================
    // DUPLICATE USER
    // =========================
    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateUser(
            DuplicateUserException ex,
            HttpServletRequest request) {

        log.error("Duplicate user error: {}", ex.getMessage());

        return new ResponseEntity<>(
                buildError(ex.getMessage(), HttpStatus.BAD_REQUEST, request.getRequestURI()),
                HttpStatus.BAD_REQUEST
        );
    }

    // =========================
    // BAD REQUEST
    // =========================
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        log.error("Invalid input: {}", ex.getMessage());

        return new ResponseEntity<>(
                buildError(ex.getMessage(), HttpStatus.BAD_REQUEST, request.getRequestURI()),
                HttpStatus.BAD_REQUEST
        );
    }

    // =========================
    // VALIDATION ERROR (@Valid)
    // =========================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String errorMsg = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(err -> err.getField() + " : " + err.getDefaultMessage())
                .orElse("Validation failed");

        log.error("Validation error: {}", errorMsg);

        return new ResponseEntity<>(
                buildError(errorMsg, HttpStatus.BAD_REQUEST, request.getRequestURI()),
                HttpStatus.BAD_REQUEST
        );
    }

    // =========================
    // GENERIC ERROR
    // =========================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unexpected error occurred", ex);

        return new ResponseEntity<>(
                buildError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}