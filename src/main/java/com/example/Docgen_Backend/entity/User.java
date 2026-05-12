package com.example.Docgen_Backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Basic Info
    private String company;
    private String identity;
    private String fullName;
    private String employeeId;

    // Contact Info
    private String mobileNo;
    private String email;
    private String panNo;
    private String dateOfBirth;

    // Address
    private String currentAddress;
    private String permanentAddress;

    // Employment Details
    private String offerDate;
    private String joiningDate;
    private Double joiningCtc;
    private Double currentCtc;
    private String joiningDesignation;
    private String currentDesignation;
    private String department;

    // Bank Details
    private String bankName;
    private String accountNo;
    private String pfType;

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    private List<Document> documents;
}