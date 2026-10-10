package com.example.Docgen_Backend.controller;

import com.example.Docgen_Backend.dto.AdminUpdateUserRequest;
import com.example.Docgen_Backend.dto.ApiResponse;
import com.example.Docgen_Backend.dto.CreateUserRequest;
import com.example.Docgen_Backend.dto.UserDto;
import com.example.Docgen_Backend.entity.Role;
import com.example.Docgen_Backend.entity.User;
import com.example.Docgen_Backend.repository.UsersRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        List<UserDto> users = usersRepository
                .findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream().map(UserDto::from).toList();
        return ResponseEntity.ok(new ApiResponse<>(true, 200, "user fetched successfully", users));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserDto>> signup(@Valid @RequestBody CreateUserRequest req) {
        String email = req.email().trim().toLowerCase();
        if (usersRepository.existsByEmailIgnoreCase(email)) {
            return ResponseEntity.status(409)
                    .body(new ApiResponse<>(false, 409, "User already exists", null));
        }

        Role role = Role.USER;
        if (req.role() != null && !req.role().isBlank()) {
            try {
                role = Role.valueOf(req.role().trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, 400, "Invalid role", null));
            }
        }

        User user = new User();
        user.setName(req.name().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setRole(role);
        usersRepository.save(user);

        return ResponseEntity.status(201)
                .body(new ApiResponse<>(true, 201, "User created successfully", UserDto.from(user)));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(
            @PathVariable String id,
            @Valid @RequestBody AdminUpdateUserRequest req,
            Authentication auth) {

        User user = usersRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>(false, 404, "User not found", null));
        }
        boolean self = user.getId().equals(auth.getName());

        if (req.name() != null && !req.name().isBlank()) user.setName(req.name().trim());

        if (req.email() != null && !req.email().isBlank()) {
            String email = req.email().trim().toLowerCase();
            if (!email.equalsIgnoreCase(user.getEmail())
                    && usersRepository.existsByEmailIgnoreCase(email)) {
                return ResponseEntity.status(409).body(new ApiResponse<>(false, 409, "Email already in use", null));
            }
            user.setEmail(email);
        }

        if (req.role() != null && !req.role().isBlank()) {
            Role role;
            try {
                role = Role.valueOf(req.role().trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, 400, "Invalid role", null));
            }
            if (self && role != Role.ADMIN) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, 400, "You cannot remove your own admin role", null));
            }
            user.setRole(role);
        }

        if (req.enabled() != null) {
            if (self && !req.enabled()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, 400, "You cannot disable your own account", null));
            }
            user.setEnabled(req.enabled());
        }

        usersRepository.save(user);
        return ResponseEntity.ok(new ApiResponse<>(true, 200, "User updated successfully", UserDto.from(user)));
    }
}