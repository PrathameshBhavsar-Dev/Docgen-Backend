package com.example.Docgen_Backend.service.impl;

import com.example.Docgen_Backend.dto.*;
import com.example.Docgen_Backend.entity.*;
import com.example.Docgen_Backend.exception.UserNotFoundException;
import com.example.Docgen_Backend.vendor.repository.VendorProfileRepository;
import com.example.Docgen_Backend.service.VendorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class VendorProfileServiceImpl extends BaseProfileService {

    private static final String VENDOR_TM = "vendorTransactionManager";

    private final VendorProfileRepository vendorProfileRepository;
    private final VendorService vendorService;

    @Transactional(transactionManager = VENDOR_TM)
    public void importFromForm(CreateProfileRequest request, String providedSecret) {
        if (providedSecret == null || providedSecret.isBlank()) {
            throw new IllegalArgumentException("Invalid import secret");
        }
        Vendor vendor = vendorService.requireActiveBySecret(providedSecret);

        request.setEmployeeId("PENDING");
        validateRequest(request);

        if (vendorProfileRepository.existsByVendorIdAndEmailIgnoreCase(
                vendor.getId(), request.getEmail().trim())) {
            throw new IllegalArgumentException("This profile was already submitted");
        }

        UserProfile user = buildUser(request);
        user.setVendorId(vendor.getId());
        user.setSource("VENDOR");
        vendorProfileRepository.save(user);

        log.info("Vendor profile imported | vendorCode={} | employeeName={}",
                vendor.getCode(), request.getEmployeeName());
    }

    public Page<VendorProfileSummary> list(Long vendorId, String search,
                                           int page, int size, String sortBy, String direction) {
        Pageable pageable = buildPageable(page, size, sortBy, direction);
        String q = search == null ? "" : search.trim();
        Map<Long, VendorResponse> vendors = vendorService.mapById();

        return vendorProfileRepository.search(vendorId, q, pageable).map(u -> {
            VendorResponse v = vendors.get(u.getVendorId());
            return new VendorProfileSummary(
                    u.getId(), u.getEmployeeName(), u.getEmployeeId(), u.getEmail(), u.getPhone(),
                    u.getDepartment(), u.getCurrentDesignation(), enumName(u.getCompany()), u.getJoiningDate(),
                    u.getLastWorkingDate(), u.getSource(), u.getCreatedAt(),
                    u.getVendorId(),
                    v == null ? null : v.code(), v == null ? null : v.name());
        });
    }

    @Transactional(readOnly = true, transactionManager = VENDOR_TM)
    public UserProfileResponseDTO getForEdit(Long id) {
        UserProfile user = vendorProfileRepository.findByIdAndVendorIdIsNotNull(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return toEditResponse(user);
    }

    @Transactional(transactionManager = VENDOR_TM)
    public void update(Long id, CreateProfileRequest request) {
        validateForUpdate(request);
        UserProfile user = vendorProfileRepository.findByIdAndVendorIdIsNotNull(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        applyUpdate(user, request);
        vendorProfileRepository.save(user);
    }
}