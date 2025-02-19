package com.example.Sweet_Dream.repository;

import com.example.Sweet_Dream.entity.EmailVerification;
import com.example.Sweet_Dream.entity.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findByEmailAndStatus(String email, VerificationStatus status);
}
