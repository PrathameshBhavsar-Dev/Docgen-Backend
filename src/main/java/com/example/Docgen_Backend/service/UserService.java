package com.example.Docgen_Backend.service;

import com.example.Docgen_Backend.dto.CreateProfileRequest;
import com.example.Docgen_Backend.dto.UserProfileResponseDTO;
import com.example.Docgen_Backend.entity.UserProfile;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface UserService {

    void createProfile(CreateProfileRequest request);
    UserProfileResponseDTO getUserForEdit(Long id);
    UserProfile getUserById(Long id);
    void updateProfile(Long id, CreateProfileRequest request);
    Page<UserProfile> getAllUserProfiles(int page, int size, String sortBy, String direction);

}