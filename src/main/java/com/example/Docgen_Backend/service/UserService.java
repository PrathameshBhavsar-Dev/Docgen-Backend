package com.example.Docgen_Backend.service;

import com.example.Docgen_Backend.dto.UserProfileRequest;
import com.example.Docgen_Backend.entity.User;

public interface UserService {
    User saveUser(UserProfileRequest userProfileRequest);
}
