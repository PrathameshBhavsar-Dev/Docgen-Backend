package com.example.Docgen_Backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class GenericDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String documentType; // e.g. SALARY_SLIP

    @Column(columnDefinition = "TEXT")
    private String metadata; // JSON

    private LocalDate createdAt;

    @ManyToOne
    @JoinColumn(name = "user_profile_id")
    private UserProfile userProfile;
}