package com.example.Docgen_Backend.controller;

import com.example.Docgen_Backend.dto.ApiResponse;
import com.example.Docgen_Backend.dto.CreateProfileRequest;
import com.example.Docgen_Backend.entity.UserProfile;
import com.example.Docgen_Backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v2/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/create-profile")
    public ResponseEntity<ApiResponse<Object>> createProfile(
            @RequestBody CreateProfileRequest request
    ) {
        userService.createProfile(request);

        ApiResponse<Object> response = new ApiResponse<>(
                true,
                201,
                "Profile created successfully",
                Map.of("employeeId", request.getEmployeeId())
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> getProfileById(
            @PathVariable Long id
    ) {
        UserProfile profile = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        ApiResponse<Object> response = new ApiResponse<>(
                true,
                200,
                "Profile fetched successfully",
                profile
        );

        return ResponseEntity.ok(response);
    }
}