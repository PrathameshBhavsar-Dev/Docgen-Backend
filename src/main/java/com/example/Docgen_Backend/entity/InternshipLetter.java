package com.example.Docgen_Backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class InternshipLetter extends BaseDocument {

    @Enumerated(EnumType.STRING)
    private InternshipType internshipType;

    private LocalDate startDate;
    private LocalDate endDate;
}