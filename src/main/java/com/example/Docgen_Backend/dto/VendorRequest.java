package com.example.Docgen_Backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VendorRequest(@NotBlank @Size(max = 100) String name) {}
