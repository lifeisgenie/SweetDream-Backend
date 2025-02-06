package com.example.Sweet_Dream.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    private SecretKey secretKey;

    // 생성자 방식으로 JWTUtil을 호출하고 application.properties에 저장되어 있는 변수 데이터를 가지고 와서 secret이라는 변수에 담는다.
    public JWTUtil(@Value("${spring.jwt.secret}")String secret) {

        // application.properties에 저장되어 있는 키를 불러와서 그 키를 기반으로 객체 키 생성.
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
    }

    public String getUsername(String token) {
        return Jwts.parser()  // parser() 사용
                .setSigningKey(secretKey)  // 서명 키 설정
                .parseClaimsJws(token)  // JWT 파싱
                .getBody()  // JWT body 가져오기
                .get("username", String.class);  // "username" 값 추출
    }

    public String getRole(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    public Boolean isExpired(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }

    public String createJwt(String username, String role, Long expiredMs) {
        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }
}

