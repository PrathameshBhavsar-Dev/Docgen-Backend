package com.example.Docgen_Backend.exception;

public class PdfGenerationException extends RuntimeException {

    public PdfGenerationException(String message) {
        super(message);
    }
}