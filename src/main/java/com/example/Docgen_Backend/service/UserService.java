package com.example.Docgen_Backend.service;

import com.example.Docgen_Backend.dto.CreateProfileRequest;
import com.example.Docgen_Backend.entity.UserProfile;

import java.util.Optional;

public interface UserService {
    void createProfile(CreateProfileRequest request);
    Optional<UserProfile> getUserById(Long id);
}