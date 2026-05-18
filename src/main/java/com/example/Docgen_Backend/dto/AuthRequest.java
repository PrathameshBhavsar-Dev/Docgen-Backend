package com.example.Docgen_Backend.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}