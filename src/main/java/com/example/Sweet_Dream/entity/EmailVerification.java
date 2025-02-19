package com.example.Sweet_Dream.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class EmailVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String otp;

    @Enumerated(EnumType.STRING)
    private VerificationStatus status = VerificationStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    public EmailVerification(String email, String otp, LocalDateTime expiredAt) {
        this.email = email;
        this.otp = otp;
        this.expiredAt = expiredAt;
    }
}

