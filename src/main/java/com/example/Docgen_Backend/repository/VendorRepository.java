package com.example.Docgen_Backend.repository;

import com.example.Docgen_Backend.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findBySecretHashAndActiveTrue(String secretHash);
    boolean existsByNameIgnoreCase(String name);
}