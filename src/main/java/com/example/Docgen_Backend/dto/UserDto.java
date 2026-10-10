package com.example.Docgen_Backend.dto;

import com.example.Docgen_Backend.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record UserDto(String id, String name, String email, String role,
                      LocalDateTime createdAt, LocalDateTime updatedAt) {

    public static UserDto from(User u) {
        return new UserDto(u.getId(), u.getName(), u.getEmail(),
                u.getRole().name().toLowerCase(), u.getCreatedAt(), u.getUpdatedAt());
    }

    // temporary: keeps old frontend code that reads user._id working
    @JsonProperty("_id")
    public String legacyId() { return id; }
}