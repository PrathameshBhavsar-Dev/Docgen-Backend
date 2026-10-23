package com.example.Docgen_Backend.dto;

import java.time.LocalDateTime;

public record VendorResponse(Long id, String name, String code,
                             boolean active, LocalDateTime createdAt) {}