package com.example.Sweet_Dream.service;

import com.example.Sweet_Dream.dto.request.RequestSignUpDTO;
import com.example.Sweet_Dream.dto.response.ResponseSignUpDTO;
import com.example.Sweet_Dream.entity.Role;
import com.example.Sweet_Dream.entity.RoleName;
import com.example.Sweet_Dream.entity.User;
import com.example.Sweet_Dream.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseSignUpDTO signUp(RequestSignUpDTO requestSignUpDTO) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(requestSignUpDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(requestSignUpDTO.getPassword());

        // User 객체 생성
        User user = new User();
        user.setUserId(requestSignUpDTO.getUserId());
        user.setUsername(requestSignUpDTO.getUsername());
        user.setEmail(requestSignUpDTO.getEmail());
        user.setPassword(encryptedPassword);

        // 역할 설정 (기본 역할 예: USER)
        Role role = new Role();
        role.setRoleName(RoleName.USER);
        user.setRole(role);

        // DB에 저장
        userRepository.save(user);

        // 응답 DTO 생성
        return new ResponseSignUpDTO(user.getUserId(), user.getUsername(), user.getEmail());
    }
}
