package com.example.Docgen_Backend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponse {
    private boolean success;
    private String message;
    private int statusCode;
    private String path;
    private LocalDateTime timestamp;
}