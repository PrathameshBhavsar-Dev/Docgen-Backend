package com.example.Docgen_Backend.entity;

import jakarta.persistence.Entity;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class ExperienceLetter extends BaseDocument {

    private LocalDate relievingDate;
}