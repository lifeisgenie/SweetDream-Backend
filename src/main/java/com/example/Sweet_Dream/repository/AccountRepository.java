package com.example.Sweet_Dream.repository;

import com.example.Sweet_Dream.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface AccountRepository extends JpaRepository<User, String> {
    // 이메일 중복 체크를 위한 메서드 추가
    boolean existsByEmail(String email);

    // userId로 User 찾기
    User findByUserId(String userId);

    boolean existsByUserId(String userId);  // userId로 존재 여부 확인

    // 이름, 이메일로 조회하기
    Optional<User> findByUsernameAndEmail(String username, String email);
}