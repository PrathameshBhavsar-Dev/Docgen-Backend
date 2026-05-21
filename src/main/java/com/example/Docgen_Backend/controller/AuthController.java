package com.example.Docgen_Backend.controller;

import com.example.Docgen_Backend.config.JwtUtil;
import com.example.Docgen_Backend.dto.ApiResponse;
import com.example.Docgen_Backend.dto.AuthRequest;
import com.example.Docgen_Backend.dto.AuthResponse;
import com.example.Docgen_Backend.dto.RegisterRequest;
import com.example.Docgen_Backend.entity.Role;
import com.example.Docgen_Backend.entity.User;
import com.example.Docgen_Backend.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ✅ REGISTER API
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Object>> register(@RequestBody RegisterRequest request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse<>(
                            false,
                            400,
                            "Username already exists",
                            null
                    ));
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.valueOf(request.getRole().toUpperCase()));

        userRepository.save(user);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        200,
                        "User registered successfully",
                        null
                )
        );
    }

    // ✅ LOGIN API
//    @PostMapping("/login")
//    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest request) {
//
//        authManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        request.getUsername(),
//                        request.getPassword()
//                )
//        );
//
//        User user = userRepository.findByUsername(request.getUsername()).get();
//
//        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
//
//        AuthResponse authResponse = new AuthResponse(
//                token,
//                user.getUsername(),
//                user.getRole().name()
//        );
//
//        return ResponseEntity.ok(
//                new ApiResponse<>(
//                        true,
//                        200,
//                        "Login successful",
//                        authResponse
//                )
//        );
//    }
}