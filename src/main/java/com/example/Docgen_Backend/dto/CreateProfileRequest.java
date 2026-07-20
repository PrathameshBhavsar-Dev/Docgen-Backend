package com.example.Docgen_Backend.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class CreateProfileRequest {

    // User Profile
    private String employeeName;
    private String email;
    private String mobileNo;
    private String employeeId;

    // Job
    private String joiningDesignation;
    private String currentDesignation;
    private String department;

    // Address
    private String currentAddress;
    private String permanentAddress;

    // Bank
    private String accountNo;
    private String bankName;

    // Salary
    private Double joiningCTC;
    private Double currentCTC;

    // Personal
    private String dateOfBirth;
    private String offerDate;
    private String joiningDate;
    private String panNo;

    // Company
    private String company;
    private String pfType;
    private String identity;

    // =========================
    // DOCUMENT SELECTION
    // =========================
    private List<String> documents;

    // =========================
    // DOCUMENT DATA
    // =========================
    /*
     Example structure:

     {
       "OFFER_LETTER": {
           "issueDate": "2026-05-12",
           "probationPeriod": 6
       },
       "INTERNSHIP_LETTER": {
           "internshipType": "PAID",
           "startDate": "2026-01-01",
           "endDate": "2026-06-01",
           "issueDate": "2026-01-01"
       }
     }
    */
    private Map<String, Object> documentData;
}