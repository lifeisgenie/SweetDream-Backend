package com.example.Sweet_Dream.service;

import com.example.Sweet_Dream.dto.request.RequestSignUpDTO;
import com.example.Sweet_Dream.dto.response.ResponseSignUpDTO;
import com.example.Sweet_Dream.entity.Role;
import com.example.Sweet_Dream.entity.RoleName;
import com.example.Sweet_Dream.entity.User;
import com.example.Sweet_Dream.repository.RoleRepository;
import com.example.Sweet_Dream.repository.AccountRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AccountService(AccountRepository accountRepository, BCryptPasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public ResponseSignUpDTO signUp(RequestSignUpDTO requestSignUpDTO) {
        // ID와 이메일 중복 확인
        if (accountRepository.existsByUserId(requestSignUpDTO.getUserId())) {
            throw new RuntimeException("User ID already exists");
        }
        if (accountRepository.existsByEmail(requestSignUpDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Role이 없는 경우 기본적으로 USER 역할을 부여
        Role role = roleRepository.findByRoleName(RoleName.USER);
        if (role == null) {
            role = new Role();
            role.setRoleName(RoleName.USER);
            role.setCreatedAt(LocalDateTime.now());
            roleRepository.save(role);  // 새 Role을 DB에 저장
        }

        // User 객체 생성
        User user = new User();
        user.setUserId(requestSignUpDTO.getUserId());
        user.setUsername(requestSignUpDTO.getUsername());
        user.setEmail(requestSignUpDTO.getEmail());
        user.setPassword(passwordEncoder.encode(requestSignUpDTO.getPassword()));  // 비밀번호 암호화
        user.setRole(role);  // Role 설정

        // User 저장
        accountRepository.save(user);

        // Response DTO 반환
        return new ResponseSignUpDTO(user.getUserId(), user.getUsername(), user.getEmail());
    }
}
