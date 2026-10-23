package com.example.Docgen_Backend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record VendorProfileSummary(
        Long id, String employeeName, String employeeId, String email, String phone,
        String department, String currentDesignation, String company, String joiningDate,
        LocalDate lastWorkingDate, String source, LocalDateTime createdAt,
        Long vendorId, String vendorCode, String vendorName) {}