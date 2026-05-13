package com.example.Docgen_Backend.entity;

import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class IncrementLetter extends BaseDocument {
    // only issueDate → inherited
}