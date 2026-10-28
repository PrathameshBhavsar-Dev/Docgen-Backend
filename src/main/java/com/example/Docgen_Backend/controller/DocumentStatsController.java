package com.example.Docgen_Backend.controller;

import com.example.Docgen_Backend.dto.AnalyticsResponse;
import com.example.Docgen_Backend.dto.DocumentStatsResponse;
import com.example.Docgen_Backend.service.DocumentStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/stats")
@RequiredArgsConstructor
public class DocumentStatsController {

    private final DocumentStatsService statsService;

    @GetMapping("/documents")
    public DocumentStatsResponse documents() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return statsService.forUser(userId);
    }

    @GetMapping("/overview")
    public AnalyticsResponse overview() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return statsService.analytics(userId);
    }
}