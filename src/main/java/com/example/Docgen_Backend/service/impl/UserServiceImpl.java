package com.example.Docgen_Backend.service.impl;

import com.example.Docgen_Backend.dto.CreateProfileRequest;
import com.example.Docgen_Backend.dto.DocumentResponseDTO;
import com.example.Docgen_Backend.dto.UserProfileResponseDTO;
import com.example.Docgen_Backend.entity.*;
import com.example.Docgen_Backend.exception.UserNotFoundException;
import com.example.Docgen_Backend.repository.CompanyEmployeeCounterRepository;
import com.example.Docgen_Backend.repository.UserRepository;
import com.example.Docgen_Backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends BaseProfileService implements UserService {

    private final UserRepository userRepository;
    private final CompanyEmployeeCounterRepository companyEmployeeCounterRepository;

    // =========================
    // CREATE PROFILE
    // =========================
    @Override
    @Transactional
    public void createProfile(CreateProfileRequest request) {

        try {
            validateRequest(request);

            CompanyType companyType = CompanyType.fromFullName(request.getCompany());

            // ✅ TEMPORARILY DISABLED — auto-generate employeeId
            // String generatedEmployeeId = generateEmployeeId(companyType);
            // log.info("Generated employeeId={} for company={}", generatedEmployeeId, request.getCompany());
            // request.setEmployeeId(generatedEmployeeId);

            UserProfile user = buildUser(request);

            // ✅ capture creator
            String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
            user.setCreatedByUserId(currentUserId);

            processDocuments(request, user);

            userRepository.save(user);

            log.info("User profile created successfully for employeeId={} | createdBy={}",
                    request.getEmployeeId(), currentUserId);

        } catch (Exception ex) {
            log.error("Error while creating profile | error={}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @Transactional
    public String generateEmployeeId(CompanyType companyType) {

        String prefix = companyType.getEmpIdPrefix();

        CompanyEmployeeCounter counter = companyEmployeeCounterRepository
                .findByPrefixForUpdate(prefix)
                .orElseGet(() -> {
                    CompanyEmployeeCounter newCounter = new CompanyEmployeeCounter();
                    newCounter.setCompanyPrefix(prefix);
                    newCounter.setLastNumber(0);
                    return newCounter;
                });

        int nextNumber = counter.getLastNumber() + 1;
        counter.setLastNumber(nextNumber);
        companyEmployeeCounterRepository.save(counter);

        return prefix + String.format("%03d", nextNumber);
    }

    // =========================
    // GET USER
    // =========================
    @Override
    public UserProfile getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with id: " + id)
                );
    }

    // =========================
    // PAGINATION
    // =========================
    private static final Set<String> SORTABLE_FIELDS = Set.of(
            "id", "employeeName", "employeeId", "email",
            "department", "joiningDate", "lastWorkingDate");

    @Override
    public Page<UserProfile> getAllUserProfiles(int page, int size, String sortBy, String direction, String search) {

        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

        // ✅ only allow known sort fields (null-safe)
        String sortField = (sortBy != null && SORTABLE_FIELDS.contains(sortBy)) ? sortBy : "id";

        Sort sort = "asc".equalsIgnoreCase(direction)
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        // ✅ clamp paging values
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);

        Pageable pageable = PageRequest.of(safePage, safeSize, sort);

        if (search != null && !search.trim().isEmpty()) {
            return userRepository.searchVisibleToUser(currentUserId, search.trim(), pageable);
        }
        return userRepository.findAllVisibleToUser(currentUserId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponseDTO getUserForEdit(Long id) {
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserProfile user = userRepository.findVisibleById(id, currentUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return toEditResponse(user);
    }

    @Override
    @Transactional
    public void updateProfile(Long id, CreateProfileRequest request) {
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        validateForUpdate(request);
        UserProfile user = userRepository.findVisibleById(id, currentUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        applyUpdate(user, request);
        userRepository.save(user);
    }

    @Value("${import.form.secret}")
    private String expectedImportSecret;

    @Value("${import.form.owner-user-id}")
    private String importOwnerUserId;

    @Transactional
    public void importFromForm(CreateProfileRequest request, String providedSecret) {

        if (!expectedImportSecret.equals(providedSecret)) {
            throw new IllegalArgumentException("Invalid import secret");
        }

        // ✅ Employee ID is filled in manually later inside the app — not collected from the form
        request.setEmployeeId("PENDING");

        validateRequest(request);

        UserProfile user = buildUser(request);
        user.setCreatedByUserId(importOwnerUserId);
        user.setSource("FORM");

        userRepository.save(user);

        log.info("Profile imported from form | employeeId=PENDING | employeeName={}", request.getEmployeeName());
    }
}