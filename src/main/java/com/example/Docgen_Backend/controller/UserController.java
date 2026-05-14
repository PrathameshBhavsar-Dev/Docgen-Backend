package com.example.Docgen_Backend.controller;

import com.example.Docgen_Backend.dto.ApiResponse;
import com.example.Docgen_Backend.dto.CreateProfileRequest;
import com.example.Docgen_Backend.entity.UserProfile;
import com.example.Docgen_Backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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

    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getAllUserProfiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {

        Page<UserProfile> pageResult =
                userService.getAllUserProfiles(page, size, sortBy, direction);

        Map<String, Object> data = new HashMap<>();
        data.put("content", pageResult.getContent());
        data.put("currentPage", pageResult.getNumber());
        data.put("totalItems", pageResult.getTotalElements());
        data.put("totalPages", pageResult.getTotalPages());

        ApiResponse<Object> response = new ApiResponse<>(
                true,
                200,
                "Profiles fetched successfully",
                data
        );

        return ResponseEntity.ok(response);
    }

}