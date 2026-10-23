package com.example.Docgen_Backend.vendor.repository;

import com.example.Docgen_Backend.entity.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VendorProfileRepository extends JpaRepository<UserProfile, Long> {

    boolean existsByVendorIdAndEmailIgnoreCase(Long vendorId, String email);

    Optional<UserProfile> findByIdAndVendorIdIsNotNull(Long id);

    @Query("""
        select u from UserProfile u
        where u.vendorId is not null
          and (:vendorId is null or u.vendorId = :vendorId)
          and (:q = '' or lower(u.employeeName) like lower(concat('%', :q, '%'))
                       or lower(u.email)        like lower(concat('%', :q, '%'))
                       or lower(u.employeeId)   like lower(concat('%', :q, '%')))
        """)
    Page<UserProfile> search(@Param("vendorId") Long vendorId,
                             @Param("q") String q, Pageable pageable);
}