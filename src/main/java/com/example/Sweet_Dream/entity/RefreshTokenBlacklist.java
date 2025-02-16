package com.example.Sweet_Dream.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_token_blacklist")
@Getter
@NoArgsConstructor
public class RefreshTokenBlacklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long blacklistId;  // 블랙리스트 레코드 ID

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "refresh_token_id", referencedColumnName = "tokenId", nullable = false)
    private RefreshToken refreshToken;  // 블랙리스트에 등록된 리프레시 토큰

    @Column(nullable = false)
    private LocalDateTime blacklistedAt;  // 블랙리스트에 등록된 시간

    @Column(nullable = false)
    private LocalDateTime revokedAt;  // 리프레시 토큰이 무효화된 시간

    @Column(nullable = false, length = 255)
    private String reason;  // 블랙리스트 사유

    public RefreshTokenBlacklist(RefreshToken refreshToken, LocalDateTime blacklistedAt, LocalDateTime revokedAt, String reason) {
        this.refreshToken = refreshToken;
        this.blacklistedAt = blacklistedAt;
        this.revokedAt = revokedAt;
        this.reason = reason;
    }
}

