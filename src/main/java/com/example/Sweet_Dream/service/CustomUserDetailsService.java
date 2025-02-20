package com.example.Sweet_Dream.service;

import com.example.Sweet_Dream.entity.CustomUserDetails;
import com.example.Sweet_Dream.entity.User;
import com.example.Sweet_Dream.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    // 구현 방법은 DB에서 특정 User를 조회해서 리턴을 해야 되므로 DB 연결을 진행
    // DB에 접근할 리포지토리 객체 UserRepository 변수 선언
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        User userData = accountRepository.findByUserId(userId);

        if (userData != null) {
            return new CustomUserDetails(userData);
        }

        throw new UsernameNotFoundException("User not found with user_id: " + userId);
    }
}
