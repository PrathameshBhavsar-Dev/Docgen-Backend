package com.example.Docgen_Backend.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String role; // ADMIN / USER
}