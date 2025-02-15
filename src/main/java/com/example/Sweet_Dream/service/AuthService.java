package com.example.Sweet_Dream.service;

import com.example.Sweet_Dream.entity.RefreshToken;
import com.example.Sweet_Dream.entity.RoleName;
import com.example.Sweet_Dream.exception.InvalidTokenException;
import com.example.Sweet_Dream.jwt.JWTUtil;
import com.example.Sweet_Dream.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public AuthService(JWTUtil jwtUtil, RefreshTokenRepository refreshTokenRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    // 액세스 토큰 갱신
    public String refreshAccessToken(String userId, String refreshToken) {
        // 1. 리프레시 토큰이 DB에 존재하는지 확인
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByTokenValue(refreshToken);
        if (optionalRefreshToken.isEmpty()) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        RefreshToken validToken = optionalRefreshToken.get();

        // 2. 만료된 토큰이면 삭제 후 예외 처리
        if (validToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(validToken);  // 만료된 토큰 삭제
            throw new InvalidTokenException("Expired refresh token");
        }

        // 3. 사용자 ID로 사용자 정보 조회 (여기서 `role`을 추출)
        String username = validToken.getUser().getUsername();
        RoleName role = validToken.getUser().getRole().getRoleName(); // 사용자에서 role 정보 추출

        // 4. 새로운 액세스 토큰 발급
        return jwtUtil.createAccessToken(username, role, 60 * 60 * 10L);  // 10시간 유효
    }

    // 로그아웃 처리 (리프레시 토큰 삭제)
    public void logout(String refreshToken) {
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByTokenValue(refreshToken);

        // 리프레시 토큰이 존재하지 않으면 예외 처리
        if (optionalRefreshToken.isEmpty()) {
            throw new InvalidTokenException("Invalid refresh token for logout");
        }

        // 리프레시 토큰 삭제
        refreshTokenRepository.delete(optionalRefreshToken.get());
    }
}
