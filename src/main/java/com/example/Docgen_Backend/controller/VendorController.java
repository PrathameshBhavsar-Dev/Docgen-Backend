package com.example.Docgen_Backend.controller;

import com.example.Docgen_Backend.dto.VendorRequest;
import com.example.Docgen_Backend.dto.VendorResponse;
import com.example.Docgen_Backend.dto.VendorSecretResponse;
import com.example.Docgen_Backend.service.VendorService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/vendors")
public class VendorController {

    private final VendorService vendorService;

    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @PostMapping
    public VendorSecretResponse create(@Valid @RequestBody VendorRequest req) {
        return vendorService.create(req.name());
    }

    @GetMapping
    public List<VendorResponse> list() { return vendorService.list(); }

    @PatchMapping("/{id}/active")
    public VendorResponse setActive(@PathVariable Long id, @RequestParam boolean value) {
        return vendorService.setActive(id, value);
    }

    @PostMapping("/{id}/rotate-secret")
    public VendorSecretResponse rotate(@PathVariable Long id) {
        return vendorService.rotateSecret(id);
    }
}