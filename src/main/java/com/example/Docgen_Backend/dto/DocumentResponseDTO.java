package com.example.Docgen_Backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class DocumentResponseDTO {

    private boolean generated;

    private Map<String, Object> data;
}