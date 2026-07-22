package com.example.Docgen_Backend.repository;

import com.example.Docgen_Backend.entity.CompanyEmployeeCounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CompanyEmployeeCounterRepository extends JpaRepository<CompanyEmployeeCounter, String> {

    @Query(value = "SELECT * FROM company_employee_counter WHERE company_prefix = :prefix FOR UPDATE",
            nativeQuery = true)
    Optional<CompanyEmployeeCounter> findByPrefixForUpdate(@Param("prefix") String prefix);
}