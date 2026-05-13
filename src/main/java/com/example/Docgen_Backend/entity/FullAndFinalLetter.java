package com.example.Docgen_Backend.entity;

import jakarta.persistence.Entity;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class FullAndFinalLetter extends BaseDocument {

    private LocalDate fnfDate;
    private String month;

    private LocalDate resignationDate;
    private LocalDate leavingDate;

    private Integer paidDays;
    private Integer totalDaysInMonth;
}