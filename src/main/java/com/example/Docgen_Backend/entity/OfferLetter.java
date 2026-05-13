package com.example.Docgen_Backend.entity;

import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class OfferLetter extends BaseDocument {

    private Integer probationPeriod; // in months
}