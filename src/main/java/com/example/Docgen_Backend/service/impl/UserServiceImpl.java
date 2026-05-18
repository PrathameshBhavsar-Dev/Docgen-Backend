package com.example.Docgen_Backend.service.impl;

import com.example.Docgen_Backend.dto.CreateProfileRequest;
import com.example.Docgen_Backend.entity.*;
import com.example.Docgen_Backend.exception.UserNotFoundException;
import com.example.Docgen_Backend.repository.UserRepository;
import com.example.Docgen_Backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public void createProfile(CreateProfileRequest request) {

        // =========================
        // 1️⃣ VALIDATION
        // =========================
        if (request.getEmployeeName() == null || request.getEmployeeName().isEmpty()) {
            throw new IllegalArgumentException("Employee name is required");
        }

        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        // =========================
        // 2️⃣ CREATE USER PROFILE
        // =========================
        UserProfile user = new UserProfile();

        try {
            user.setEmployeeName(request.getEmployeeName());
            user.setEmail(request.getEmail());
            user.setPhone(request.getMobileNo());
            user.setEmployeeId(request.getEmployeeId());
            user.setDesignation(request.getDesignation());
            user.setDepartment(request.getDepartment());
            user.setAccountNo(request.getAccountNo());
            user.setBankName(request.getBankName());
            user.setAddress(request.getAddress());
            user.setCTC(request.getCTC());
            user.setDateOfBirth(request.getDateOfBirth());
            user.setOfferDate(request.getOfferDate());
            user.setJoiningDate(request.getJoiningDate());
            user.setPanNo(request.getPanNo());

            // ENUM SAFE PARSING
            user.setIdentity(
                    IdentityType.valueOf(request.getIdentity().toUpperCase())
            );

            user.setCompany(
                    CompanyType.fromFullName(request.getCompany())
            );

            user.setPfType(
                    PFType.valueOf(request.getPfType().toUpperCase())
            );

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid enum or input value provided");
        }

        // =========================
        // 3️⃣ HANDLE DOCUMENTS
        // =========================
        if (request.getDocuments() == null || request.getDocuments().isEmpty()) {
            throw new IllegalArgumentException("At least one document must be provided");
        }

        for (String doc : request.getDocuments()) {

            Map<String, Object> data =
                    (Map<String, Object>) request.getDocumentData().get(doc);

            if (data == null) {
                throw new IllegalArgumentException("Missing data for document: " + doc);
            }

            try {
                switch (doc) {

                    // ================= OFFER LETTER =================
                    case "OFFER_LETTER":

                        if (data.get("issueDate") == null || data.get("probationPeriod") == null) {
                            throw new IllegalArgumentException("Missing fields in OFFER_LETTER");
                        }

                        OfferLetter offer = new OfferLetter();

                        offer.setIssueDate(
                                LocalDate.parse((String) data.get("issueDate"))
                        );

                        offer.setProbationPeriod(
                                Integer.parseInt(data.get("probationPeriod").toString())
                        );

                        offer.setUserProfile(user);
                        user.setOfferLetter(offer);

                        break;

                    // ================= INTERNSHIP LETTER =================
                    case "INTERNSHIP_LETTER":

                        InternshipLetter internship = new InternshipLetter();

                        internship.setInternshipType(
                                InternshipType.valueOf((String) data.get("internshipType"))
                        );

                        internship.setStartDate(
                                LocalDate.parse((String) data.get("startDate"))
                        );

                        internship.setEndDate(
                                LocalDate.parse((String) data.get("endDate"))
                        );

                        internship.setIssueDate(
                                LocalDate.parse((String) data.get("issueDate"))
                        );

                        internship.setUserProfile(user);
                        user.setInternshipLetter(internship);

                        break;

                    // ================= SALARY SLIP =================
                    case "SALARY_SLIP":

                        SalarySlip salary = new SalarySlip();

                        salary.setStartMonth((String) data.get("startMonth"));
                        salary.setEndMonth((String) data.get("endMonth"));

                        salary.setUserProfile(user);
                        user.getSalarySlips().add(salary);

                        break;

                    default:
                        throw new IllegalArgumentException("Invalid document type: " + doc);
                }

            } catch (Exception e) {
                throw new RuntimeException("Error processing document: " + doc + " - " + e.getMessage());
            }
        }

        // =========================
        // 4️⃣ SAVE
        // =========================
        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Database error while saving user profile");
        }
    }

    @Override
    public UserProfile getUserById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with id: " + id)
                );
    }

    @Override
    public Page<UserProfile> getAllUserProfiles(int page, int size, String sortBy, String direction) {

        Sort sort = direction.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return userRepository.findAll(pageable);
    }

}