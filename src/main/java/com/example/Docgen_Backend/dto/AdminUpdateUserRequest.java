package com.example.Docgen_Backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record AdminUpdateUserRequest(
        @Size(min = 2) String name, @Email String email, String role, Boolean enabled) {}