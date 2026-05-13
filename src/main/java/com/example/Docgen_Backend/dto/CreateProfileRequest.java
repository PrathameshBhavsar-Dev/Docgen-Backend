package com.example.Docgen_Backend.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class CreateProfileRequest {

    // =========================
    // USER PROFILE
    // =========================
    private String employeeName;
    private String email;
    private String mobileNo;
    private String employeeId;
    private String designation;
    private String department;
    private String accountNo;
    private String bankName;
    private String address;
    private Double CTC;
    private String dateOfBirth;
    private String offerDate;
    private String joiningDate;
    private String panNo;


    // ✅ NEW
    private String company;   // full name (e.g. "SmartMatrix Digital Services Pvt. Ltd.")
    private String pfType;    // WITH_PF / WITHOUT_PF

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