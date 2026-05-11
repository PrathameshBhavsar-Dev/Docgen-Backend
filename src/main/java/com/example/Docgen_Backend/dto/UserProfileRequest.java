package com.example.Docgen_Backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserProfileRequest {

    private String company;
    private String identity;
    private String fullName;
    private String employeeId;

    private String mobileNo;
    private String email;
    private String panNo;
    private String dateOfBirth;

    private String currentAddress;
    private String permanentAddress;

    private String offerDate;
    private String joiningDate;
    private Double joiningCtc;
    private Double currentCtc;
    private String joiningDesignation;
    private String currentDesignation;
    private String department;

    private String bankName;
    private String accountNo;
    private String pfType;

    private List<String> documents; // selected docs
}