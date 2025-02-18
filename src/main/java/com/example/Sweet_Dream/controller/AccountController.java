package com.example.Sweet_Dream.controller;

import com.example.Sweet_Dream.dto.request.FindIdRequestDTO;
import com.example.Sweet_Dream.dto.request.RequestSignUpDTO;
import com.example.Sweet_Dream.dto.response.FindIdResponseDTO;
import com.example.Sweet_Dream.dto.response.ResponseSignUpDTO;
import com.example.Sweet_Dream.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ResponseSignUpDTO> signUp(@RequestBody RequestSignUpDTO requestSignUpDTO) {
        ResponseSignUpDTO response = accountService.signUp(requestSignUpDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)  // 응답 타입을 명시적으로 설정
                .body(response);
    }

    @PostMapping("/findid")
    public ResponseEntity<FindIdResponseDTO> findId(@RequestBody FindIdRequestDTO request) {
        FindIdResponseDTO response = accountService.findId(request);
        return ResponseEntity.status(response.getResultCode().equals("201000") ? HttpStatus.OK : HttpStatus.NOT_FOUND)
                .body(response);
    }

}
