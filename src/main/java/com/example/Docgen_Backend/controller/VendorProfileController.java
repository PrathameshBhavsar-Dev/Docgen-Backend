package com.example.Docgen_Backend.controller;

import com.example.Docgen_Backend.dto.*;
import com.example.Docgen_Backend.service.impl.VendorProfileServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/vendor-profiles")
@RequiredArgsConstructor
public class VendorProfileController {

    private final VendorProfileServiceImpl service;

    @PostMapping("/import-form")
    public ResponseEntity<Void> importForm(@RequestHeader("X-Import-Secret") String secret,
                                           @RequestBody CreateProfileRequest request) {
        service.importFromForm(request, secret);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public Page<VendorProfileSummary> list(
            @RequestParam(required = false) Long vendorId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        return service.list(vendorId, search, page, size, sortBy, direction);
    }

    @GetMapping("/{id}")
    public UserProfileResponseDTO get(@PathVariable Long id) { return service.getForEdit(id); }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody CreateProfileRequest request) {
        service.update(id, request);
        return ResponseEntity.ok().build();
    }
}