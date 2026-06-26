package com.example.Docgen_Backend.repository;

import com.example.Docgen_Backend.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserProfile, Long> {
    boolean existsByEmployeeIdAndEmployeeNameAndEmail(
            String employeeId,
            String employeeName,
            String email);
}
