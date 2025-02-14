package com.example.Sweet_Dream.repository;

import com.example.Sweet_Dream.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenValue(String tokenValue);  // 토큰값으로 리프레시 토큰 찾기
}
