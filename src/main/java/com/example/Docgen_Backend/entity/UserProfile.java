package com.example.Docgen_Backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // BASIC INFO
    private String employeeName;
    private String employeeId;
    private String email;
    private String phone;

    // PERSONAL INFO
    private String panNo;
    private String dateOfBirth;

    // ADDRESS
    private String address;

    // JOB INFO
    private String designation;
    private String department;

    private String offerDate;
    private String joiningDate;

    private Double CTC;

    // BANK DETAILS
    private String bankName;
    private String accountNo;

    @Enumerated(EnumType.STRING)
    private IdentityType identity;

    // 🔥 IMPORTANT: PF TYPE (GLOBAL)
    @Enumerated(EnumType.STRING)
    private PFType pfType;

    // COMPANY
    @Enumerated(EnumType.STRING)
    private CompanyType company;

    // ================= RELATIONS =================

    @OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private OfferLetter offerLetter;

    @OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private AppointmentLetter appointmentLetter;

    @OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private ExperienceLetter experienceLetter;

    @OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private RelievingLetter relievingLetter;

    @OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private IncrementLetter incrementLetter;

    @OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private InternshipLetter internshipLetter;

    @OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private CompletionLetter completionLetter;

    @OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private FullAndFinalLetter fullAndFinalLetter;

    @OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private ConfirmationLetter confirmationLetter;

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<SalarySlip> salarySlips = new ArrayList<>();
}