package com.example.Docgen_Backend.dto;

import java.util.List;
import java.util.Map;

public record AnalyticsResponse(
        long totalDocuments,
        Map<String, Long> byType,        // entity name -> count
        Map<String, Long> byCompany,     // company enum name -> count
        List<Long> monthlyCounts,        // 12 values, Jan..Dec of the current year
        List<DailyCount> last7Days) {}