package com.example.Docgen_Backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class UserProfileResponseDTO {

    private Long id;

    private String employeeName;
    private String employeeId;
    private String email;
    private String mobileNo;

    private String designation;
    private String department;

    private String company;
    private String identity;
    private String pfType;

    private Map<String, DocumentResponseDTO> documents;
}