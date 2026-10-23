package com.example.Docgen_Backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);

                if (jwtUtil.validateToken(token)) {
                    String userId = jwtUtil.extractUserId(token);
                    String role = normalizeRole(jwtUtil.extractRole(token));

                    if (userId != null && role != null) {
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userId, null,
                                        List.of(new SimpleGrantedAuthority("ROLE_" + role)));
                        authentication.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        log.warn("JWT has no usable userId/role");
                    }
                } else {
                    log.warn("Invalid JWT token");
                }
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                log.error("JWT authentication failed: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);   // called exactly once
    }

    private static String normalizeRole(String role) {
        if (role == null || role.isBlank()) return null;
        String r = role.trim().toUpperCase();
        return r.startsWith("ROLE_") ? r.substring(5) : r;
    }
}