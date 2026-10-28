package com.example.Docgen_Backend.dto;

public record DocumentStatsResponse(long totalDocuments, long thisMonthDocuments, long lastMonthDocuments) {}