package com.example.Sweet_Dream.service;

import com.example.Sweet_Dream.dto.request.RequestSignUpDTO;
import com.example.Sweet_Dream.dto.response.ResponseSignUpDTO;
import com.example.Sweet_Dream.dto.request.FindIdRequestDTO;
import com.example.Sweet_Dream.dto.response.FindIdResponseDTO;
import com.example.Sweet_Dream.entity.Role;
import com.example.Sweet_Dream.entity.RoleName;
import com.example.Sweet_Dream.entity.User;
import com.example.Sweet_Dream.repository.RoleRepository;
import com.example.Sweet_Dream.repository.AccountRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

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


    public FindIdResponseDTO findId(FindIdRequestDTO request) {
        Optional<User> user = accountRepository.findByUsernameAndEmail(request.getUsername(), request.getEmail());
        // 이름과 이메일로 사용자를 검색

        if (user.isPresent()) { // 사용자 존재
            return FindIdResponseDTO.builder()
                    .resultCode("201000") // 성공 코드
                    .resultMessage("인증 성공")
                    .userId(user.get().getUserId()) // 조회한 사용자 ID값 포함
                    .build();
        } else { // 사용자 없을 경우
            return FindIdResponseDTO.builder()
                    .resultCode("404000")
                    .resultMessage("인증 정보 없음")
                    .userId(null) // id를 널로 반환
                    .build();
        }
    }
}
