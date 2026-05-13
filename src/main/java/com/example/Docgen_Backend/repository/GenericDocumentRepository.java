package com.example.Docgen_Backend.repository;

import com.example.Docgen_Backend.entity.GenericDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenericDocumentRepository extends JpaRepository<GenericDocument, Long> {
}