package com.example.Docgen_Backend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class UserProfileResponseDTO {

    private Long id;

    private String employeeName;
    private String employeeId;
    private String email;
    private String mobileNo;

    // Job Info
    private String joiningDesignation;
    private String currentDesignation;
    private String department;

    // Company Info
    private String company;
    private String identity;
    private String pfType;

    // Bank Info
    private String accountNo;
    private String bankName;

    // Address
    private String currentAddress;
    private String permanentAddress;

    // Salary
    private Double joiningCTC;
    private Double currentCTC;

    // Personal
    private String dateOfBirth;
    private String offerDate;
    private String joiningDate;
    private String panNo;

    private LocalDateTime createdAt;

    private Map<String, DocumentResponseDTO> documents;
}