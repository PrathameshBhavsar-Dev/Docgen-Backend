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
    public class UserServiceImpl implements UserService {
    
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
        // VALIDATION
        // =========================
        private void validateRequest(CreateProfileRequest request) {

            // ✅ RE-ENABLED — manual employeeId entry temporarily
            if (request.getEmployeeId() == null || request.getEmployeeId().trim().isEmpty()) {
                throw new IllegalArgumentException("Employee ID is required");
            }

            if (request.getEmployeeName() == null || request.getEmployeeName().trim().isEmpty()) {
                throw new IllegalArgumentException("Employee name is required");
            }

            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                throw new IllegalArgumentException("Email is required");
            }

            if (request.getCompany() == null) {
                throw new IllegalArgumentException("Company is required");
            }
        }

        private <E extends Enum<E>> E parseEnum(Class<E> type, String value, String label) {
            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException(label + " is required");
            }
            try {
                return Enum.valueOf(type, value.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid " + label + ": " + value);
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
                user.setJoiningDesignation(request.getJoiningDesignation());
                user.setCurrentDesignation(request.getCurrentDesignation());
                user.setDepartment(request.getDepartment());

                user.setAccountNo(request.getAccountNo());
                user.setBankName(request.getBankName());

                user.setCurrentAddress(request.getCurrentAddress());
                user.setPermanentAddress(request.getPermanentAddress());

                user.setJoiningCTC(request.getJoiningCTC());
                user.setCurrentCTC(request.getCurrentCTC());
                user.setDateOfBirth(request.getDateOfBirth());
                user.setOfferDate(request.getOfferDate());
                user.setJoiningDate(request.getJoiningDate());
                user.setLastWorkingDate(request.getLastWorkingDate());
                user.setPanNo(request.getPanNo());

                user.setIdentity(parseEnum(IdentityType.class, request.getIdentity(), "identity"));
                user.setCompany(CompanyType.fromFullName(request.getCompany()));
                user.setPfType(parseEnum(PFType.class, request.getPfType(), "PF type"));

                user.setSource("MANUAL");
    
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
    
                    case "OFFER_LETTER" -> handleOfferLetter(user, data);
                    case "APPOINTMENT_LETTER" -> handleAppointmentLetter(user, data);
                    case "INTERNSHIP_CERTIFICATE" -> handleInternshipLetter(user, data);
                    case "INCREMENT_LETTER" -> handleIncrementLetter(user, data);
                    case "COMPLETION_CERTIFICATE" -> handleCompletionLetter(user, data);
                    case "CONFIRMATION_LETTER" -> handleConfirmationLetter(user, data);
                    case "EXPERIENCE_LETTER" -> handleExperienceLetter(user, data);
                    case "RELIEVING_LETTER" -> handleRelievingLetter(user, data);
                    case "FULL_AND_FINAL_LETTER" -> handleFullAndFinal(user, data);
                    case "SALARY_SLIP" -> handleSalarySlip(user, data);
                    case "GENERIC" -> handleGenericDocument(user, data, doc);
    
                    default -> throw new IllegalArgumentException("Invalid document type: " + doc);
                }
            }
        }
    
        // =========================
        // 🔥 COMMON HELPERS (IMPORTANT)
        // =========================
    
        private String getRequiredString(Map<String, Object> data, String key) {
            Object value = data.get(key);
            if (value == null) {
                throw new IllegalArgumentException(key + " is required");
            }
            return value.toString();
        }
    
        private Integer getRequiredInteger(Map<String, Object> data, String key) {
            Object value = data.get(key);
            if (value == null) {
                throw new IllegalArgumentException(key + " is required");
            }
            return Integer.parseInt(value.toString());
        }
    
        private LocalDate getRequiredDate(Map<String, Object> data, String key) {
            Object value = data.get(key);
    
            if (value == null || value.toString().isBlank()) {
                throw new IllegalArgumentException(key + " is required");
            }
    
            return LocalDate.parse(value.toString());
        }
    
        // =========================
        // DOCUMENT HANDLERS
        // =========================
    
        private void handleIncrementLetter(UserProfile user, Map<String, Object> data) {
            IncrementLetter offer = new IncrementLetter();
            offer.setIssueDate(getRequiredDate(data, "issueDate"));
            offer.setUserProfile(user);
            user.setIncrementLetter(offer);
        }
    
        private void handleOfferLetter(UserProfile user, Map<String, Object> data) {
            OfferLetter offer = new OfferLetter();
            offer.setProbationPeriod(getRequiredInteger(data, "probationPeriod"));
            offer.setIssueDate(getRequiredDate(data, "issueDate"));
            offer.setUserProfile(user);
            user.setOfferLetter(offer);
        }
    
        private void handleAppointmentLetter(UserProfile user, Map<String, Object> data) {
            AppointmentLetter letter = new AppointmentLetter();
            letter.setProbationPeriod(getRequiredInteger(data, "probationPeriod"));
            letter.setIssueDate(getRequiredDate(data, "issueDate"));
            letter.setUserProfile(user);
            user.setAppointmentLetter(letter);
        }
    
        private void handleInternshipLetter(UserProfile user, Map<String, Object> data) {
            InternshipLetter internship = new InternshipLetter();
    
            internship.setInternshipType(
                    InternshipType.valueOf(getRequiredString(data, "internshipType"))
            );
    
            internship.setStartDate(getRequiredDate(data, "startDate"));
            internship.setEndDate(getRequiredDate(data, "endDate"));
            internship.setIssueDate(getRequiredDate(data, "issueDate"));
    
            internship.setUserProfile(user);
            user.setInternshipLetter(internship);
        }
    
        private void handleCompletionLetter(UserProfile user, Map<String, Object> data) {
            CompletionLetter letter = new CompletionLetter();
            letter.setStartDate(getRequiredDate(data, "startDate"));
            letter.setCompletionDate(getRequiredDate(data, "completionDate"));
            letter.setIssueDate(getRequiredDate(data, "issueDate"));
            letter.setUserProfile(user);
            user.setCompletionLetter(letter);
        }
    
        private void handleConfirmationLetter(UserProfile user, Map<String, Object> data) {
            ConfirmationLetter letter = new ConfirmationLetter();
            letter.setEffectiveDate(getRequiredDate(data, "effectiveDate"));
            letter.setIssueDate(getRequiredDate(data, "issueDate"));
            letter.setUserProfile(user);
            user.setConfirmationLetter(letter);
        }
    
        private void handleExperienceLetter(UserProfile user, Map<String, Object> data) {
            ExperienceLetter letter = new ExperienceLetter();
            letter.setRelievingDate(getRequiredDate(data, "relievingDate"));
            letter.setIssueDate(getRequiredDate(data, "issueDate"));
            letter.setUserProfile(user);
            user.setExperienceLetter(letter);
        }
    
        private void handleRelievingLetter(UserProfile user, Map<String, Object> data) {
            RelievingLetter letter = new RelievingLetter();
            letter.setRelievingDate(getRequiredDate(data, "relievingDate"));
            letter.setIssueDate(getRequiredDate(data, "issueDate"));
            letter.setUserProfile(user);
            user.setRelievingLetter(letter);
        }
    
        private void handleFullAndFinal(UserProfile user, Map<String, Object> data) {
            FullAndFinalLetter fnf = new FullAndFinalLetter();
    
            fnf.setFnfDate(getRequiredDate(data, "fnfDate"));
            fnf.setIssueDate(getRequiredDate(data, "issueDate"));
            fnf.setMonth(getRequiredString(data, "month"));
            fnf.setResignationDate(getRequiredDate(data, "resignationDate"));
            fnf.setLeavingDate(getRequiredDate(data, "leavingDate"));
            fnf.setPaidDays(getRequiredInteger(data, "paidDays"));
            fnf.setTotalDaysInMonth(getRequiredInteger(data, "totalDaysInMonth"));
    
            fnf.setUserProfile(user);
            user.setFullAndFinalLetter(fnf);
        }
    
        private void handleSalarySlip(UserProfile user, Map<String, Object> data) {
            SalarySlip salary = new SalarySlip();
    
            salary.setStartMonth(getRequiredString(data, "startMonth"));
            salary.setEndMonth(getRequiredString(data, "endMonth"));
    
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

        private String enumName(Enum<?> e) {
            return e == null ? null : e.name();
        }

        @Override
        @Transactional(readOnly = true)
        public UserProfileResponseDTO getUserForEdit(Long id) {

            String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

            log.info("Fetching user profile for edit | userId={} | requestedBy={}", id, currentUserId);

            long start = System.currentTimeMillis();

            try {

                UserProfile user = userRepository.findVisibleById(id, currentUserId)
                        .orElseThrow(() -> {
                            log.warn("User not found or not owned by requester | userId={} | requestedBy={}", id, currentUserId);
                            return new UserNotFoundException("User not found");
                        });
    
                log.debug("User found | userId={} | employeeId={}",
                        user.getId(), user.getEmployeeId());
    
                Map<String, DocumentResponseDTO> docs = new HashMap<>();
    
                docs.put("OFFER_LETTER", buildOfferLetter(user));
                docs.put("APPOINTMENT_LETTER", buildAppointmentLetter(user));
                docs.put("INCREMENT_LETTER", buildIncrementLetter(user));
                docs.put("INTERNSHIP_LETTER", buildInternshipLetter(user));
                docs.put("COMPLETION_LETTER", buildCompletionLetter(user));
                docs.put("CONFIRMATION_LETTER", buildConfirmationLetter(user));
                docs.put("EXPERIENCE_LETTER", buildExperienceLetter(user));
                docs.put("RELIEVING_LETTER", buildRelievingLetter(user));
                docs.put("FULL_AND_FINAL", buildFullAndFinalLetter(user));
                docs.put("SALARY_SLIP", buildSalarySlip(user));

                UserProfileResponseDTO response = UserProfileResponseDTO.builder()
                        .id(user.getId())

                        .employeeName(user.getEmployeeName())
                        .employeeId(user.getEmployeeId())
                        .email(user.getEmail())
                        .mobileNo(user.getPhone())

                        .joiningDesignation(user.getJoiningDesignation())
                        .currentDesignation(user.getCurrentDesignation())
                        .department(user.getDepartment())

                        .company(enumName(user.getCompany()))
                        .identity(enumName(user.getIdentity()))
                        .pfType(enumName(user.getPfType()))

                        .accountNo(user.getAccountNo())
                        .bankName(user.getBankName())

                        .currentAddress(user.getCurrentAddress())
                        .permanentAddress(user.getPermanentAddress())

                        .joiningCTC(user.getJoiningCTC())
                        .currentCTC(user.getCurrentCTC())
                        .dateOfBirth(user.getDateOfBirth())
                        .offerDate(user.getOfferDate())
                        .joiningDate(user.getJoiningDate())
                        .lastWorkingDate(user.getLastWorkingDate())
                        .panNo(user.getPanNo())

                        .documents(docs)
                        .build();
    
                long end = System.currentTimeMillis();
    
                log.info("User profile fetched successfully | userId={} | timeTaken={}ms",
                        id, (end - start));
    
                return response;
    
            } catch (Exception ex) {
    
                log.error("Error while fetching user profile | userId={} | error={}",
                        id, ex.getMessage(), ex);
    
                throw ex; // Let GlobalExceptionHandler handle response
            }
        }
    
        private DocumentResponseDTO buildOfferLetter(UserProfile user) {
    
            OfferLetter letter = user.getOfferLetter();
    
            if (letter == null) {
                return DocumentResponseDTO.builder()
                        .generated(false)
                        .data(null)
                        .build();
            }
    
            Map<String, Object> data = new HashMap<>();
    
            data.put("issueDate", letter.getIssueDate());
            data.put("probationPeriod", letter.getProbationPeriod());
    
            return DocumentResponseDTO.builder()
                    .generated(true)
                    .data(data)
                    .build();
        }
    
        private DocumentResponseDTO buildAppointmentLetter(UserProfile user) {
    
            AppointmentLetter letter = user.getAppointmentLetter();
    
            if (letter == null) {
                return DocumentResponseDTO.builder()
                        .generated(false)
                        .data(null)
                        .build();
            }
    
            Map<String, Object> data = new HashMap<>();
    
            data.put("issueDate", letter.getIssueDate());
            data.put("probationPeriod", letter.getProbationPeriod());
    
            return DocumentResponseDTO.builder()
                    .generated(true)
                    .data(data)
                    .build();
        }
    
        private DocumentResponseDTO buildIncrementLetter(UserProfile user) {
    
            IncrementLetter letter = user.getIncrementLetter();
    
            if (letter == null) {
                return DocumentResponseDTO.builder()
                        .generated(false)
                        .data(null)
                        .build();
            }
    
            Map<String, Object> data = new HashMap<>();
    
            data.put("issueDate", letter.getIssueDate());
    
            return DocumentResponseDTO.builder()
                    .generated(true)
                    .data(data)
                    .build();
        }
    
        private DocumentResponseDTO buildInternshipLetter(UserProfile user) {
    
            InternshipLetter letter = user.getInternshipLetter();
    
            if (letter == null) {
                return DocumentResponseDTO.builder()
                        .generated(false)
                        .data(null)
                        .build();
            }
    
            Map<String, Object> data = new HashMap<>();
    
            data.put("issueDate", letter.getIssueDate());
            data.put("startDate", letter.getStartDate());
            data.put("endDate", letter.getEndDate());
            data.put("internshipType", letter.getInternshipType());
    
            return DocumentResponseDTO.builder()
                    .generated(true)
                    .data(data)
                    .build();
        }
    
        private DocumentResponseDTO buildCompletionLetter(UserProfile user) {
    
            CompletionLetter letter = user.getCompletionLetter();
    
            if (letter == null) {
                return DocumentResponseDTO.builder()
                        .generated(false)
                        .data(null)
                        .build();
            }
    
            Map<String, Object> data = new HashMap<>();
    
            data.put("issueDate", letter.getIssueDate());
            data.put("startDate", letter.getStartDate());
            data.put("completionDate", letter.getCompletionDate());
    
            return DocumentResponseDTO.builder()
                    .generated(true)
                    .data(data)
                    .build();
        }
    
        private DocumentResponseDTO buildConfirmationLetter(UserProfile user) {
    
            ConfirmationLetter letter = user.getConfirmationLetter();
    
            if (letter == null) {
                return DocumentResponseDTO.builder()
                        .generated(false)
                        .data(null)
                        .build();
            }
    
            Map<String, Object> data = new HashMap<>();
    
            data.put("issueDate", letter.getIssueDate());
            data.put("effectiveDate", letter.getEffectiveDate());
    
            return DocumentResponseDTO.builder()
                    .generated(true)
                    .data(data)
                    .build();
        }
    
        private DocumentResponseDTO buildExperienceLetter(UserProfile user) {
    
            ExperienceLetter letter = user.getExperienceLetter();
    
            if (letter == null) {
                return DocumentResponseDTO.builder()
                        .generated(false)
                        .data(null)
                        .build();
            }
    
            Map<String, Object> data = new HashMap<>();
    
            data.put("issueDate", letter.getIssueDate());
            data.put("relievingDate", letter.getRelievingDate());
    
            return DocumentResponseDTO.builder()
                    .generated(true)
                    .data(data)
                    .build();
        }
    
        private DocumentResponseDTO buildRelievingLetter(UserProfile user) {
    
            RelievingLetter letter = user.getRelievingLetter();
    
            if (letter == null) {
                return DocumentResponseDTO.builder()
                        .generated(false)
                        .data(null)
                        .build();
            }
    
            Map<String, Object> data = new HashMap<>();
    
            data.put("issueDate", letter.getIssueDate());
            data.put("relievingDate", letter.getRelievingDate());
    
            return DocumentResponseDTO.builder()
                    .generated(true)
                    .data(data)
                    .build();
        }
    
        private DocumentResponseDTO buildFullAndFinalLetter(UserProfile user) {
    
            FullAndFinalLetter letter = user.getFullAndFinalLetter();
    
            if (letter == null) {
                return DocumentResponseDTO.builder()
                        .generated(false)
                        .data(null)
                        .build();
            }
    
            Map<String, Object> data = new HashMap<>();
    
            data.put("issueDate", letter.getIssueDate());
            data.put("fnfDate", letter.getFnfDate());
            data.put("month", letter.getMonth());
            data.put("resignationDate", letter.getResignationDate());
            data.put("leavingDate", letter.getLeavingDate());
            data.put("paidDays", letter.getPaidDays());
            data.put("totalDaysInMonth", letter.getTotalDaysInMonth());
    
            return DocumentResponseDTO.builder()
                    .generated(true)
                    .data(data)
                    .build();
        }
    
        private DocumentResponseDTO buildSalarySlip(UserProfile user) {
    
            if (user.getSalarySlips() == null ||
                    user.getSalarySlips().isEmpty()) {
    
                return DocumentResponseDTO.builder()
                        .generated(false)
                        .data(null)
                        .build();
            }
    
            SalarySlip slip = user.getSalarySlips().get(0);
    
            Map<String, Object> data = new HashMap<>();
    
            data.put("startMonth", slip.getStartMonth());
            data.put("endMonth", slip.getEndMonth());
    
            return DocumentResponseDTO.builder()
                    .generated(true)
                    .data(data)
                    .build();
        }

        @Override
        @Transactional
        public void updateProfile(Long id, CreateProfileRequest request) {

            String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

            log.info("Updating user profile | userId={} | employeeId={} | requestedBy={}",
                    id, request.getEmployeeId(), currentUserId);

            long start = System.currentTimeMillis();

            try {
                validateRequest(request);

                if ("PENDING".equalsIgnoreCase(request.getEmployeeId().trim())) {
                    throw new IllegalArgumentException("Please enter the actual Employee ID");
                }

                UserProfile user = userRepository.findVisibleById(id, currentUserId)
                        .orElseThrow(() -> {
                            log.warn("User not found or not accessible for update | userId={} | requestedBy={}",
                                    id, currentUserId);
                            return new UserNotFoundException("User not found");
                        });

                log.debug("Existing user fetched | userId={} | oldEmployeeId={}",
                        user.getId(), user.getEmployeeId());

                // ========================
                // UPDATE BASIC PROFILE
                // ========================
                user.setEmployeeName(request.getEmployeeName());
                user.setEmail(request.getEmail());
                user.setPhone(request.getMobileNo());
                user.setEmployeeId(request.getEmployeeId());
                user.setJoiningDesignation(request.getJoiningDesignation());
                user.setCurrentDesignation(request.getCurrentDesignation());
                user.setDepartment(request.getDepartment());

                user.setAccountNo(request.getAccountNo());
                user.setBankName(request.getBankName());

                user.setCurrentAddress(request.getCurrentAddress());
                user.setPermanentAddress(request.getPermanentAddress());

                user.setJoiningCTC(request.getJoiningCTC());
                user.setCurrentCTC(request.getCurrentCTC());
                user.setDateOfBirth(request.getDateOfBirth());
                user.setOfferDate(request.getOfferDate());
                user.setJoiningDate(request.getJoiningDate());
                user.setLastWorkingDate(request.getLastWorkingDate());
                user.setPanNo(request.getPanNo());

                // ========================
                // SAFE ENUM MAPPING
                // ========================
                try {
                    user.setIdentity(parseEnum(IdentityType.class, request.getIdentity(), "identity"));
                } catch (Exception e) {
                    log.error("Invalid identity value | value={}", request.getIdentity());
                    throw new IllegalArgumentException("Invalid identity value");
                }

                try {
                    user.setCompany(
                            CompanyType.fromFullName(request.getCompany())
                    );
                } catch (Exception e) {
                    log.error("Invalid company value | value={}", request.getCompany());
                    throw new IllegalArgumentException("Invalid company value");
                }

                try {
                    user.setPfType(parseEnum(PFType.class, request.getPfType(), "PF type"));
                } catch (Exception e) {
                    log.error("Invalid PF type value | value={}", request.getPfType());
                    throw new IllegalArgumentException("Invalid PF type value");
                }

                // ========================
                // UPDATE DOCUMENTS
                // ========================
                log.debug("Updating documents | userId={}", id);
                processUpdateDocuments(request, user);

                // ========================
                // SAVE
                // ========================
                userRepository.save(user);

                long end = System.currentTimeMillis();

                log.info("User profile updated successfully | userId={} | timeTaken={}ms",
                        id, (end - start));

            } catch (Exception ex) {

                log.error("Error while updating user profile | userId={} | error={}",
                        id, ex.getMessage(), ex);

                throw ex;
            }
        }

        private Map<String, Object> getDocData(CreateProfileRequest request, String doc) {
            if (request.getDocumentData() == null
                    || !(request.getDocumentData().get(doc) instanceof Map<?, ?> raw)) {
                throw new IllegalArgumentException("Missing data for document: " + doc);
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) raw;
            return data;
        }

        private void processUpdateDocuments(CreateProfileRequest request,
                                            UserProfile user) {

            // ✅ NEW: fail early. A missing list would otherwise throw an NPE below,
            // and treating null as "empty" would silently delete every document.
            if (request.getDocuments() == null) {
                throw new IllegalArgumentException("Documents list is required");
            }

            // REMOVE DOCS THAT ARE UNCHECKED
            removeUncheckedDocuments(request, user);

            for (String doc : request.getDocuments()) {

                // ✅ CHANGED: null-safe, no unchecked cast
                Map<String, Object> data = getDocData(request, doc);

                switch (doc) {

                    case "OFFER_LETTER" ->
                            upsertOfferLetter(user, data);

                    case "APPOINTMENT_LETTER" ->
                            upsertAppointmentLetter(user, data);

                    case "INCREMENT_LETTER" ->
                            upsertIncrementLetter(user, data);

                    case "INTERNSHIP_LETTER" ->
                            upsertInternshipLetter(user, data);

                    case "COMPLETION_LETTER" ->
                            upsertCompletionLetter(user, data);

                    case "CONFIRMATION_LETTER" ->
                            upsertConfirmationLetter(user, data);

                    case "EXPERIENCE_LETTER" ->
                            upsertExperienceLetter(user, data);

                    case "RELIEVING_LETTER" ->
                            upsertRelievingLetter(user, data);

                    case "FULL_AND_FINAL" ->
                            upsertFullAndFinal(user, data);

                    case "SALARY_SLIP" ->
                            upsertSalarySlip(user, data);

                    default ->
                            throw new IllegalArgumentException(
                                    "Invalid document type: " + doc
                            );
                }
            }
        }
    
        private void upsertOfferLetter(UserProfile user,
                                       Map<String, Object> data) {
    
            OfferLetter letter = user.getOfferLetter();
    
            if (letter == null) {
                letter = new OfferLetter();
                letter.setUserProfile(user);
            }
    
            letter.setIssueDate(
                    getRequiredDate(data, "issueDate")
            );
    
            letter.setProbationPeriod(
                    getRequiredInteger(data, "probationPeriod")
            );
    
            user.setOfferLetter(letter);
        }
    
        private void upsertAppointmentLetter(UserProfile user,
                                             Map<String, Object> data) {
    
            AppointmentLetter letter = user.getAppointmentLetter();
    
            if (letter == null) {
                letter = new AppointmentLetter();
                letter.setUserProfile(user);
            }
    
            letter.setIssueDate(
                    getRequiredDate(data, "issueDate")
            );
    
            letter.setProbationPeriod(
                    getRequiredInteger(data, "probationPeriod")
            );
    
            user.setAppointmentLetter(letter);
        }
    
        private void upsertIncrementLetter(UserProfile user,
                                           Map<String, Object> data) {
    
            IncrementLetter letter = user.getIncrementLetter();
    
            if (letter == null) {
                letter = new IncrementLetter();
                letter.setUserProfile(user);
            }
    
            letter.setIssueDate(
                    getRequiredDate(data, "issueDate")
            );
    
            user.setIncrementLetter(letter);
        }
    
        private void upsertInternshipLetter(UserProfile user,
                                            Map<String, Object> data) {
    
            InternshipLetter letter = user.getInternshipLetter();
    
            if (letter == null) {
                letter = new InternshipLetter();
                letter.setUserProfile(user);
            }
    
            letter.setIssueDate(
                    getRequiredDate(data, "issueDate")
            );
    
            letter.setStartDate(
                    getRequiredDate(data, "startDate")
            );
    
            letter.setEndDate(
                    getRequiredDate(data, "endDate")
            );
    
            letter.setInternshipType(
                    InternshipType.valueOf(
                            getRequiredString(data, "internshipType")
                    )
            );
    
            user.setInternshipLetter(letter);
        }
    
        private void upsertCompletionLetter(UserProfile user,
                                            Map<String, Object> data) {
    
            CompletionLetter letter = user.getCompletionLetter();
    
            if (letter == null) {
                letter = new CompletionLetter();
                letter.setUserProfile(user);
            }
    
            letter.setIssueDate(
                    getRequiredDate(data, "issueDate")
            );
    
            letter.setStartDate(
                    getRequiredDate(data, "startDate")
            );
    
            letter.setCompletionDate(
                    getRequiredDate(data, "completionDate")
            );
    
            user.setCompletionLetter(letter);
        }
    
        private void upsertConfirmationLetter(UserProfile user,
                                              Map<String, Object> data) {
    
            ConfirmationLetter letter = user.getConfirmationLetter();
    
            if (letter == null) {
                letter = new ConfirmationLetter();
                letter.setUserProfile(user);
            }
    
            letter.setIssueDate(
                    getRequiredDate(data, "issueDate")
            );
    
            letter.setEffectiveDate(
                    getRequiredDate(data, "effectiveDate")
            );
    
            user.setConfirmationLetter(letter);
        }
    
        private void upsertExperienceLetter(UserProfile user,
                                            Map<String, Object> data) {
    
            ExperienceLetter letter = user.getExperienceLetter();
    
            if (letter == null) {
                letter = new ExperienceLetter();
                letter.setUserProfile(user);
            }
    
            letter.setIssueDate(
                    getRequiredDate(data, "issueDate")
            );
    
            letter.setRelievingDate(
                    getRequiredDate(data, "relievingDate")
            );
    
            user.setExperienceLetter(letter);
        }
    
        private void upsertRelievingLetter(UserProfile user,
                                           Map<String, Object> data) {
    
            RelievingLetter letter = user.getRelievingLetter();
    
            if (letter == null) {
                letter = new RelievingLetter();
                letter.setUserProfile(user);
            }
    
            letter.setIssueDate(
                    getRequiredDate(data, "issueDate")
            );
    
            letter.setRelievingDate(
                    getRequiredDate(data, "relievingDate")
            );
    
            user.setRelievingLetter(letter);
        }
    
        private void upsertFullAndFinal(UserProfile user,
                                        Map<String, Object> data) {
    
            FullAndFinalLetter letter = user.getFullAndFinalLetter();
    
            if (letter == null) {
                letter = new FullAndFinalLetter();
                letter.setUserProfile(user);
            }
    
            letter.setIssueDate(
                    getRequiredDate(data, "issueDate")
            );
    
            letter.setFnfDate(
                    getRequiredDate(data, "fnfDate")
            );
    
            letter.setMonth(
                    getRequiredString(data, "month")
            );
    
            letter.setResignationDate(
                    getRequiredDate(data, "resignationDate")
            );
    
            letter.setLeavingDate(
                    getRequiredDate(data, "leavingDate")
            );
    
            letter.setPaidDays(
                    getRequiredInteger(data, "paidDays")
            );
    
            letter.setTotalDaysInMonth(
                    getRequiredInteger(data, "totalDaysInMonth")
            );
    
            user.setFullAndFinalLetter(letter);
        }
    
        private void upsertSalarySlip(UserProfile user,
                                      Map<String, Object> data) {
    
            SalarySlip salary;
    
            if (user.getSalarySlips().isEmpty()) {
    
                salary = new SalarySlip();
                salary.setUserProfile(user);
    
                user.getSalarySlips().add(salary);
    
            } else {
    
                salary = user.getSalarySlips().get(0);
            }
    
            salary.setStartMonth(
                    getRequiredString(data, "startMonth")
            );
    
            salary.setEndMonth(
                    getRequiredString(data, "endMonth")
            );
        }
    
        private void removeUncheckedDocuments(CreateProfileRequest request,
                                              UserProfile user) {
    
            if (!request.getDocuments().contains("OFFER_LETTER")) {
                user.setOfferLetter(null);
            }
    
            if (!request.getDocuments().contains("APPOINTMENT_LETTER")) {
                user.setAppointmentLetter(null);
            }
    
            if (!request.getDocuments().contains("INCREMENT_LETTER")) {
                user.setIncrementLetter(null);
            }
    
            if (!request.getDocuments().contains("INTERNSHIP_LETTER")) {
                user.setInternshipLetter(null);
            }
    
            if (!request.getDocuments().contains("COMPLETION_LETTER")) {
                user.setCompletionLetter(null);
            }
    
            if (!request.getDocuments().contains("CONFIRMATION_LETTER")) {
                user.setConfirmationLetter(null);
            }
    
            if (!request.getDocuments().contains("EXPERIENCE_LETTER")) {
                user.setExperienceLetter(null);
            }
    
            if (!request.getDocuments().contains("RELIEVING_LETTER")) {
                user.setRelievingLetter(null);
            }
    
            if (!request.getDocuments().contains("FULL_AND_FINAL")) {
                user.setFullAndFinalLetter(null);
            }
    
            if (!request.getDocuments().contains("SALARY_SLIP")) {
                user.getSalarySlips().clear();
            }
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