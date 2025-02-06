package com.example.Sweet_Dream.controller;

import com.example.Sweet_Dream.dto.request.RequestSignUpDTO;
import com.example.Sweet_Dream.dto.response.ResponseSignUpDTO;
import com.example.Sweet_Dream.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ResponseSignUpDTO> signUp(@RequestBody RequestSignUpDTO requestSignUpDTO) {
        ResponseSignUpDTO response = userService.signUp(requestSignUpDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
