package com.example.Sweet_Dream.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;  // 리프레시 토큰 ID

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;  // 해당 리프레시 토큰을 가진 사용자

    @Column(nullable = false)
    private String tokenValue;  // 리프레시 토큰 값

    @Column(nullable = false)
    private LocalDateTime issuedAt;  // 발급 시간

    @Column(nullable = false)
    private LocalDateTime createdAt;  // 생성 시간

    @Column(nullable = false)
    private LocalDateTime expiresAt;  // 만료 시간

    public RefreshToken(String tokenValue, User user, LocalDateTime expiresAt) {
        this.tokenValue = tokenValue;
        this.user = user;
        this.issuedAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.expiresAt = expiresAt;
    }
}
