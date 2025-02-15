package com.example.Sweet_Dream.repository;

import com.example.Sweet_Dream.entity.RefreshToken;
import com.example.Sweet_Dream.entity.RefreshTokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenBlacklistRepository extends JpaRepository<RefreshTokenBlacklist, Long> {
    // 리프레시 토큰의 값만으로 블랙리스트에 존재하는지 확인하는 메서드 추가
    boolean existsByRefreshToken_TokenValue(String tokenValue);
}


