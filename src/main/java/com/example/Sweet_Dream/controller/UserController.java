package com.example.Sweet_Dream.controller;


import com.example.Sweet_Dream.dto.request.RequestUpdateMyPageDTO;
import com.example.Sweet_Dream.dto.response.ResponseMypageDTO;
import com.example.Sweet_Dream.dto.response.ResponseUpdateMyPageDTO;
import com.example.Sweet_Dream.entity.User;
import com.example.Sweet_Dream.service.AccountService;
import com.example.Sweet_Dream.service.AuthService;
import com.example.Sweet_Dream.service.UserService;
import lombok.RequiredArgsConstructor;

import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final AuthService authService;
    private final AccountService accountService;
    private final UserService userService;

    //마이페이지 정보 조회
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

    //마이페이지 프로필 수정
    @PatchMapping("/me")
    public ResponseEntity<ResponseUpdateMyPageDTO> updateMyPage(Authentication authentication, @RequestBody RequestUpdateMyPageDTO request) {
        String userId = authentication.getName();
        User user = accountService.getUserByUserID(userId);

        User user1 = userService.updateUser(user, request);
        return ResponseEntity.ok(new ResponseUpdateMyPageDTO(user1));

    }

    //마이페이지 회원 탈퇴
}
