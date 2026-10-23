package com.example.Docgen_Backend.service;

import com.example.Docgen_Backend.dto.VendorResponse;
import com.example.Docgen_Backend.dto.VendorSecretResponse;
import com.example.Docgen_Backend.entity.Vendor;
import com.example.Docgen_Backend.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VendorService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private final VendorRepository vendorRepository;

    @Transactional
    public VendorSecretResponse create(String name) {
        if (vendorRepository.existsByNameIgnoreCase(name.trim())) {
            throw new IllegalArgumentException("Vendor already exists");
        }
        String secret = newSecret();
        Vendor v = new Vendor();
        v.setName(name.trim());
        v.setCode(String.format("V%02d", vendorRepository.count() + 1));
        v.setSecretHash(hash(secret));
        vendorRepository.save(v);
        return new VendorSecretResponse(toResponse(v), secret);   // secret is shown only now
    }

    @Transactional(readOnly = true)
    public List<VendorResponse> list() {
        return vendorRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public VendorResponse setActive(Long id, boolean active) {
        Vendor v = get(id);
        v.setActive(active);
        return toResponse(v);
    }

    @Transactional
    public VendorSecretResponse rotateSecret(Long id) {
        Vendor v = get(id);
        String secret = newSecret();
        v.setSecretHash(hash(secret));
        return new VendorSecretResponse(toResponse(v), secret);
    }

    /** Used by the form-import endpoint later. Throws if secret is unknown or vendor inactive. */
    @Transactional(readOnly = true)
    public Vendor requireActiveBySecret(String secret) {
        return vendorRepository.findBySecretHashAndActiveTrue(hash(secret))
                .orElseThrow(() -> new AccessDeniedException("Invalid import secret"));
    }

    @Transactional(readOnly = true)
    public Map<Long, VendorResponse> mapById() {
        return list().stream().collect(Collectors.toMap(VendorResponse::id, v -> v));
    }

    private Vendor get(Long id) {
        return vendorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Vendor not found"));
    }

    private VendorResponse toResponse(Vendor v) {
        return new VendorResponse(v.getId(), v.getName(), v.getCode(), v.isActive(), v.getCreatedAt());
    }

    private static String newSecret() {
        byte[] b = new byte[32];
        RANDOM.nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }

    static String hash(String secret) {
        try {
            byte[] d = MessageDigest.getInstance("SHA-256")
                    .digest(secret.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(d);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}