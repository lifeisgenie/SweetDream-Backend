package com.example.Sweet_Dream.service;


import com.example.Sweet_Dream.dto.request.RequestUpdateMyPageDTO;
import com.example.Sweet_Dream.entity.User;
import com.example.Sweet_Dream.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AccountRepository accountRepository;

    public User updateUser(User user, RequestUpdateMyPageDTO request) {

        if(request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getImage() != null) {
            user.setImage(request.getImage());
        }

        return accountRepository.save(user);
    }

}
