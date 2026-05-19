package com.example.Docgen_Backend.service.impl;

import com.example.Docgen_Backend.dto.CreateProfileRequest;
import com.example.Docgen_Backend.entity.*;
import com.example.Docgen_Backend.exception.UserNotFoundException;
import com.example.Docgen_Backend.repository.UserRepository;
import com.example.Docgen_Backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    // =========================
    // CREATE PROFILE
    // =========================
    @Override
    public void createProfile(CreateProfileRequest request) {

        validateRequest(request);

        UserProfile user = buildUser(request);

        processDocuments(request, user);

        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Database error while saving user profile");
        }
    }

    // =========================
    // VALIDATION
    // =========================
    private void validateRequest(CreateProfileRequest request) {

        if (request.getEmployeeName() == null || request.getEmployeeName().isEmpty()) {
            throw new IllegalArgumentException("Employee name is required");
        }

        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (request.getDocuments() == null || request.getDocuments().isEmpty()) {
            throw new IllegalArgumentException("At least one document must be provided");
        }
    }

    // =========================
    // BUILD USER
    // =========================
    private UserProfile buildUser(CreateProfileRequest request) {

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

        return user;
    }

    // =========================
    // PROCESS DOCUMENTS
    // =========================
    private void processDocuments(CreateProfileRequest request, UserProfile user) {

        for (String doc : request.getDocuments()) {

            Map<String, Object> data =
                    (Map<String, Object>) request.getDocumentData().get(doc);

            if (data == null) {
                throw new IllegalArgumentException("Missing data for document: " + doc);
            }

            switch (doc) {

                case "OFFER_LETTER":
                    handleOfferLetter(user, data);
                    break;

                case "APPOINTMENT_LETTER":
                    handleAppointmentLetter(user, data);
                    break;

                case "INTERNSHIP_LETTER":
                    handleInternshipLetter(user, data);
                    break;

                case "COMPLETION_LETTER":
                    handleCompletionLetter(user, data);
                    break;

                case "CONFIRMATION_LETTER":
                    handleConfirmationLetter(user, data);
                    break;

                case "EXPERIENCE_LETTER":
                    handleExperienceLetter(user, data);
                    break;

                case "RELIEVING_LETTER":
                    handleRelievingLetter(user, data);
                    break;

                case "FULL_AND_FINAL":
                    handleFullAndFinal(user, data);
                    break;

                case "SALARY_SLIP":
                    handleSalarySlip(user, data);
                    break;

                case "GENERIC":
                    handleGenericDocument(user, data, doc);
                    break;

                default:
                    throw new IllegalArgumentException("Invalid document type: " + doc);
            }
        }
    }

    // =========================
    // DOCUMENT HANDLERS
    // =========================

    private void handleOfferLetter(UserProfile user, Map<String, Object> data) {
        OfferLetter offer = new OfferLetter();
        offer.setProbationPeriod(Integer.parseInt(data.get("probationPeriod").toString()));
        offer.setUserProfile(user);
        user.setOfferLetter(offer);
    }

    private void handleAppointmentLetter(UserProfile user, Map<String, Object> data) {
        AppointmentLetter letter = new AppointmentLetter();
        letter.setProbationPeriod(Integer.parseInt(data.get("probationPeriod").toString()));
        letter.setUserProfile(user);
        user.setAppointmentLetter(letter);
    }

    private void handleInternshipLetter(UserProfile user, Map<String, Object> data) {
        InternshipLetter internship = new InternshipLetter();

        internship.setInternshipType(
                InternshipType.valueOf((String) data.get("internshipType"))
        );

        internship.setStartDate(LocalDate.parse((String) data.get("startDate")));
        internship.setEndDate(LocalDate.parse((String) data.get("endDate")));

        internship.setUserProfile(user);
        user.setInternshipLetter(internship);
    }

    private void handleCompletionLetter(UserProfile user, Map<String, Object> data) {
        CompletionLetter letter = new CompletionLetter();
        letter.setStartDate(LocalDate.parse((String) data.get("startDate")));
        letter.setCompletionDate(LocalDate.parse((String) data.get("completionDate")));
        letter.setUserProfile(user);
        user.setCompletionLetter(letter);
    }

    private void handleConfirmationLetter(UserProfile user, Map<String, Object> data) {
        ConfirmationLetter letter = new ConfirmationLetter();
        letter.setEffectiveDate(LocalDate.parse((String) data.get("effectiveDate")));
        letter.setUserProfile(user);
        user.setConfirmationLetter(letter);
    }

    private void handleExperienceLetter(UserProfile user, Map<String, Object> data) {
        ExperienceLetter letter = new ExperienceLetter();
        letter.setRelievingDate(LocalDate.parse((String) data.get("relievingDate")));
        letter.setUserProfile(user);
        user.setExperienceLetter(letter);
    }

    private void handleRelievingLetter(UserProfile user, Map<String, Object> data) {
        RelievingLetter letter = new RelievingLetter();
        letter.setRelievingDate(LocalDate.parse((String) data.get("relievingDate")));
        letter.setUserProfile(user);
        user.setRelievingLetter(letter);
    }

    private void handleFullAndFinal(UserProfile user, Map<String, Object> data) {
        FullAndFinalLetter fnf = new FullAndFinalLetter();

        fnf.setFnfDate(LocalDate.parse((String) data.get("fnfDate")));
        fnf.setMonth((String) data.get("month"));
        fnf.setResignationDate(LocalDate.parse((String) data.get("resignationDate")));
        fnf.setLeavingDate(LocalDate.parse((String) data.get("leavingDate")));
        fnf.setPaidDays(Integer.parseInt(data.get("paidDays").toString()));
        fnf.setTotalDaysInMonth(Integer.parseInt(data.get("totalDaysInMonth").toString()));

        fnf.setUserProfile(user);
        user.setFullAndFinalLetter(fnf);
    }

    private void handleSalarySlip(UserProfile user, Map<String, Object> data) {
        SalarySlip salary = new SalarySlip();

        salary.setStartMonth((String) data.get("startMonth"));
        salary.setEndMonth((String) data.get("endMonth"));

        salary.setUserProfile(user);
        user.getSalarySlips().add(salary);
    }

    private void handleGenericDocument(UserProfile user, Map<String, Object> data, String docType) {
        GenericDocument generic = new GenericDocument();

        generic.setDocumentType(docType);
        generic.setMetadata(data.toString());
        generic.setCreatedAt(LocalDate.now());

        generic.setUserProfile(user);
        user.getGenericDocuments().add(generic);
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
    @Override
    public Page<UserProfile> getAllUserProfiles(int page, int size, String sortBy, String direction) {

        Sort sort = direction.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return userRepository.findAll(pageable);
    }
}