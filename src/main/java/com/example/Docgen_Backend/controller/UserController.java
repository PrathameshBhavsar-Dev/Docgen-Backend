package com.example.Docgen_Backend.controller;

import com.example.Docgen_Backend.dto.ApiResponse;
import com.example.Docgen_Backend.dto.CreateProfileRequest;
import com.example.Docgen_Backend.dto.UserProfileResponseDTO;
import com.example.Docgen_Backend.entity.UserProfile;
import com.example.Docgen_Backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v2/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // CREATE PROFILE
    @PostMapping("/create-profile")
    public ResponseEntity<ApiResponse<Object>> createProfile(
            @RequestBody CreateProfileRequest request
    ) {

        log.info("POST /api/v2/users/create-profile - Request received for employeeId={}", request.getEmployeeId());
        log.debug("Full request payload: {}", request);

        userService.createProfile(request);

        log.info("Profile created successfully for employeeId={}", request.getEmployeeId());

        ApiResponse<Object> response = new ApiResponse<>(
                true,
                201,
                "Profile created successfully",
                Map.of("employeeId", request.getEmployeeId())
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // GET USER BY ID FOR THE SEPARATION OF THE GENERATED DOCUMENTS
    @GetMapping("/separation/{id}")
    public ResponseEntity<ApiResponse<Object>> getProfileByIdForSeparation(
            @PathVariable Long id
    ) {

        log.info("GET /api/v2/users/{} - Fetch request received", id);

        UserProfileResponseDTO profile = userService.getUserForEdit(id);

        log.info("Profile fetched successfully for userId={}", id);
        log.debug("Fetched profile data: {}", profile);

        ApiResponse<Object> response = new ApiResponse<>(
                true,
                200,
                "Profile fetched successfully",
                profile
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> getProfileById(
            @PathVariable Long id
    ) {

        log.info("GET /api/v2/users/{} - Fetch request received", id);

        UserProfile profile = userService.getUserById(id);

        log.info("Profile fetched successfully for userId={}", id);
        log.debug("Fetched profile data: {}", profile);

        ApiResponse<Object> response = new ApiResponse<>(
                true,
                200,
                "Profile fetched successfully",
                profile
        );

        return ResponseEntity.ok(response);
    }

    // GET ALL USERS WITH PAGINATION
    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getAllUserProfiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String search
    ) {
        Page<UserProfile> pageResult =
                userService.getAllUserProfiles(page, size, sortBy, direction, search);

        List<UserProfileResponseDTO> dtoList = pageResult.getContent().stream()
                .map(user -> UserProfileResponseDTO.builder()
                        .id(user.getId())
                        .employeeName(user.getEmployeeName())
                        .employeeId(user.getEmployeeId())
                        .email(user.getEmail())
                        .mobileNo(user.getPhone())
                        .joiningDesignation(user.getJoiningDesignation())
                        .currentDesignation(user.getCurrentDesignation())
                        .department(user.getDepartment())
                        .company(user.getCompany() != null ? user.getCompany().getFullName() : null)
                        .identity(user.getIdentity() != null ? user.getIdentity().name() : null)
                        .pfType(user.getPfType() != null ? user.getPfType().name() : null)
                        .accountNo(user.getAccountNo())
                        .bankName(user.getBankName())
                        .currentAddress(user.getCurrentAddress())
                        .permanentAddress(user.getPermanentAddress())
                        .joiningCTC(user.getJoiningCTC())
                        .currentCTC(user.getCurrentCTC())
                        .dateOfBirth(user.getDateOfBirth())
                        .offerDate(user.getOfferDate())
                        .joiningDate(user.getJoiningDate())
                        .panNo(user.getPanNo())
                        .createdAt(user.getCreatedAt())
                        // .documents(...) — omit for list view; only build for single-profile fetch
                        .build())
                .collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("content", dtoList);
        data.put("currentPage", pageResult.getNumber());
        data.put("totalItems", pageResult.getTotalElements());
        data.put("totalPages", pageResult.getTotalPages());

        ApiResponse<Object> response = new ApiResponse<>(true, 200, "Profiles fetched successfully", data);
        return ResponseEntity.ok(response);
    }

    // UPDATE PROFILE + DOCUMENTS
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> updateProfile(
            @PathVariable Long id,
            @RequestBody CreateProfileRequest request
    ) {

        log.info("PATCH /api/v2/users/{} - Update request received for employeeId={}", id, request.getEmployeeId());
        log.debug("Update payload: {}", request);

        userService.updateProfile(id, request);

        log.info("Profile updated successfully for userId={}, employeeId={}", id, request.getEmployeeId());

        ApiResponse<Object> response = new ApiResponse<>(
                true,
                200,
                "Profile updated successfully",
                Map.of(
                        "userId", id,
                        "employeeId", request.getEmployeeId()
                )
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/import-form")
    public ResponseEntity<ApiResponse<Object>> importFromForm(
            @RequestHeader("X-Import-Secret") String importSecret,
            @RequestBody CreateProfileRequest request
    ) {
        userService.importFromForm(request, importSecret);
        return ResponseEntity.status(201)
                .body(new ApiResponse<>(true, 201, "Profile imported from form", null));
    }
}