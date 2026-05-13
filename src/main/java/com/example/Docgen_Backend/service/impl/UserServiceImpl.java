package com.example.Docgen_Backend.service.impl;

import com.example.Docgen_Backend.dto.CreateProfileRequest;
import com.example.Docgen_Backend.entity.*;
import com.example.Docgen_Backend.repository.UserRepository;
import com.example.Docgen_Backend.service.UserService;
import lombok.RequiredArgsConstructor;
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
        // 1️⃣ CREATE USER PROFILE
        // =========================
        UserProfile user = new UserProfile();

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


        // ✅ Company (store full name via enum)
        user.setCompany(
                CompanyType.fromFullName(request.getCompany())
        );

        // ✅ PF Type
        user.setPfType(
                PFType.valueOf(request.getPfType().toUpperCase())
        );

        // =========================
        // 2️⃣ HANDLE DOCUMENTS
        // =========================
        for (String doc : request.getDocuments()) {

            Map<String, Object> data =
                    (Map<String, Object>) request.getDocumentData().get(doc);

            if (data == null) continue;

            switch (doc) {

                // ================= OFFER LETTER =================
                case "OFFER_LETTER":

                    OfferLetter offer = new OfferLetter();
                    offer.setIssueDate(LocalDate.parse((String) data.get("issueDate")));
                    offer.setProbationPeriod(
                            Integer.parseInt(data.get("probationPeriod").toString())
                    );

                    // 🔥 BI-DIRECTIONAL LINK
                    offer.setUserProfile(user);
                    user.setOfferLetter(offer);

                    break;

                // ================= INTERNSHIP LETTER =================
                case "INTERNSHIP_LETTER":

                    InternshipLetter internship = new InternshipLetter();
                    internship.setInternshipType(
                            InternshipType.valueOf((String) data.get("internshipType"))
                    );
                    internship.setStartDate(LocalDate.parse((String) data.get("startDate")));
                    internship.setEndDate(LocalDate.parse((String) data.get("endDate")));
                    internship.setIssueDate(LocalDate.parse((String) data.get("issueDate")));

                    // 🔥 LINK
                    internship.setUserProfile(user);
                    user.setInternshipLetter(internship);

                    break;

                // ================= SALARY SLIP =================
                case "SALARY_SLIP":

                    SalarySlip salary = new SalarySlip();

                    salary.setStartMonth((String) data.get("startMonth"));
                    salary.setEndMonth((String) data.get("endMonth"));

                    // 🔥 LINK
                    salary.setUserProfile(user);

                    user.getSalarySlips().add(salary);

                    break;
            }
        }

        // =========================
        // 3️⃣ SAVE EVERYTHING (CASCADE)
        // =========================
        userRepository.save(user);
    }

    @Override
    public Optional<UserProfile> getUserById(Long id) {
        Optional<UserProfile> profile = userRepository.findById(id);
        return profile;
    }

}