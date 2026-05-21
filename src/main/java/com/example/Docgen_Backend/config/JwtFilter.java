package com.example.Docgen_Backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        String token = null;

        try {

            // =========================
            // CHECK AUTH HEADER
            // =========================

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {

                filterChain.doFilter(request, response);
                return;
            }

            // =========================
            // EXTRACT TOKEN
            // =========================

            token = authHeader.substring(7);

            // =========================
            // VALIDATE TOKEN
            // =========================

            if (!jwtUtil.validateToken(token)) {

                log.warn("Invalid JWT token");

                filterChain.doFilter(request, response);
                return;
            }

            // =========================
            // EXTRACT DATA
            // =========================

            String userId = jwtUtil.extractUserId(token);
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractRole(token);

            log.info("JWT validated successfully for userId={}", userId);

            // =========================
            // SET AUTHORITIES
            // =========================

            SimpleGrantedAuthority authority =
                    new SimpleGrantedAuthority("ROLE_" + role);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            List.of(authority)
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            // =========================
            // SET SECURITY CONTEXT
            // =========================

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

        } catch (Exception e) {

            log.error("JWT authentication failed: {}", e.getMessage());

        }

        filterChain.doFilter(request, response);
    }
}