package com.example.Docgen_Backend.exception;

public class TemplateNotFoundException extends RuntimeException{
    public TemplateNotFoundException(String message) {
        super(message);
    }
}
