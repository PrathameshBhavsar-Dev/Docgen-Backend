package com.example.Docgen_Backend.controller;

import com.example.Docgen_Backend.config.JwtUtil;
import com.example.Docgen_Backend.dto.*;
import com.example.Docgen_Backend.entity.User;
import com.example.Docgen_Backend.repository.UsersRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v2/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String REFRESH_COOKIE = "refreshToken";

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UsersRepository usersRepository;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginData>> login(
            @Valid @RequestBody LoginRequest req, HttpServletResponse res) {
        String email = req.email().trim().toLowerCase();
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, req.password()));
        } catch (AuthenticationException e) {
            return unauthorized("Invalid email or password");
        }
        User user = usersRepository.findByEmailIgnoreCase(email).orElseThrow();
        return ok("Login successful", issueTokens(user, res));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginData>> refresh(
            @CookieValue(name = REFRESH_COOKIE, required = false) String token,
            HttpServletResponse res) {
        String userId = token == null ? null : jwtUtil.validateRefreshToken(token);
        Optional<User> user = userId == null ? Optional.empty()
                : usersRepository.findById(userId).filter(User::isEnabled);
        if (user.isEmpty()) return unauthorized("Session expired. Please login again.");
        return ok("Token refreshed", issueTokens(user.get(), res));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Object>> logout(HttpServletResponse res) {
        res.addHeader(HttpHeaders.SET_COOKIE, refreshCookie("", 0).toString());
        return ResponseEntity.ok(new ApiResponse<>(true, 200, "Logged out", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> me(Authentication auth) {
        return usersRepository.findById(auth.getName())
                .map(u -> ok("User fetched successfully", UserDto.from(u)))
                .orElseGet(() -> unauthorized("User no longer exists"));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserDto>> updateProfile(
            Authentication auth, @Valid @RequestBody UpdateProfileRequest req) {
        User user = usersRepository.findById(auth.getName()).orElse(null);
        if (user == null) return unauthorized("User no longer exists");

        if (req.name() != null && !req.name().isBlank()) user.setName(req.name().trim());
        if (req.email() != null && !req.email().isBlank()) {
            String email = req.email().trim().toLowerCase();
            if (!email.equalsIgnoreCase(user.getEmail())
                    && usersRepository.existsByEmailIgnoreCase(email)) {
                return ResponseEntity.status(409)
                        .body(new ApiResponse<>(false, 409, "Email already in use", null));
            }
            user.setEmail(email);
        }
        usersRepository.save(user);
        return ok("Profile updated successfully", UserDto.from(user));
    }

    // ---------- helpers ----------

    private LoginData issueTokens(User user, HttpServletResponse res) {
        String access = jwtUtil.generateAccessToken(
                user.getId(), user.getEmail(), user.getRole().name());
        String refresh = jwtUtil.generateRefreshToken(user.getId());
        res.addHeader(HttpHeaders.SET_COOKIE,
                refreshCookie(refresh, jwtUtil.getRefreshExpirationMs() / 1000).toString());
        return new LoginData(access, UserDto.from(user));
    }

    private ResponseCookie refreshCookie(String value, long maxAgeSeconds) {
        return ResponseCookie.from(REFRESH_COOKIE, value)
                .httpOnly(true).secure(true).sameSite("None")   // Vercel -> Render is cross-site
                .path("/api/v2/auth").maxAge(maxAgeSeconds).build();
    }

    private <T> ResponseEntity<ApiResponse<T>> ok(String msg, T data) {
        return ResponseEntity.ok(new ApiResponse<>(true, 200, msg, data));
    }

    private <T> ResponseEntity<ApiResponse<T>> unauthorized(String msg) {
        return ResponseEntity.status(401).body(new ApiResponse<>(false, 401, msg, null));
    }
}