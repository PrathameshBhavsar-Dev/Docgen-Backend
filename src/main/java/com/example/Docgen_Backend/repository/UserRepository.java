package com.example.Docgen_Backend.repository;

import com.example.Docgen_Backend.entity.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserProfile, Long> {

    boolean existsByEmployeeId(String employeeId);
    boolean existsByEmail(String email);
    boolean existsByEmployeeName(String employeeName); // Only if names must also be unique
    Page<UserProfile> findAllByCreatedByUserId(String createdByUserId, Pageable pageable);
    Optional<UserProfile> findByIdAndCreatedByUserId(Long id, String createdByUserId);

    @Query("""
    SELECT u FROM UserProfile u
    WHERE u.createdByUserId = :userId
    AND (LOWER(u.employeeName) LIKE LOWER(CONCAT('%', :search, '%'))
         OR LOWER(u.employeeId) LIKE LOWER(CONCAT('%', :search, '%'))
         OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))
    """)
    Page<UserProfile> searchByCreatedByUserId(
            @Param("userId") String userId,
            @Param("search") String search,
            Pageable pageable
    );
}
