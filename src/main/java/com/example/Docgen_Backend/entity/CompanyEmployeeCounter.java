package com.example.Docgen_Backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "company_employee_counter")
@Getter
@Setter
public class CompanyEmployeeCounter {

    @Id
    @Column(name = "company_prefix", length = 10)
    private String companyPrefix;

    @Column(name = "last_number", nullable = false)
    private Integer lastNumber = 0;

    @Version
    private Long version; // optimistic locking fallback if you don't use pessimistic lock
}