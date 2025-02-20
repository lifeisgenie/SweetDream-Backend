package com.example.Sweet_Dream.controller;


import com.example.Sweet_Dream.dto.response.ResponseMypageDTO;
import com.example.Sweet_Dream.entity.User;
import com.example.Sweet_Dream.service.AccountService;
import com.example.Sweet_Dream.service.AuthService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final AuthService authService;
    private final AccountService accountService;


    @GetMapping("/me")
    public ResponseEntity<ResponseMypageDTO> signUp(Authentication authentication) {
        String userId = authentication.getName();
        User user = accountService.getUserByUserID(userId);
        ResponseMypageDTO response = new ResponseMypageDTO();
        response.setEmail(user.getEmail());
        response.setUsername(user.getUsername());
        response.setUserid(userId);
        if(user.getImage() != null){
            response.setImage(user.getImage());
        }
        else{
            response.setImage("null");
        }

        return ResponseEntity.ok(response);
    }

}
