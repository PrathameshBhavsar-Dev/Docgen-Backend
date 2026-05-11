package com.example.Docgen_Backend.repository;

import com.example.Docgen_Backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
