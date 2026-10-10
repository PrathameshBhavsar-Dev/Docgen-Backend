package com.example.Docgen_Backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(@Size(min = 2) String name, @Email String email) {}
