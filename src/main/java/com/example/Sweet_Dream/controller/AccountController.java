package com.example.Sweet_Dream.controller;

import com.example.Sweet_Dream.dto.request.RequestEmailDTO;
import com.example.Sweet_Dream.dto.request.RequestOtpVerificationDTO;
import com.example.Sweet_Dream.dto.request.RequestSignUpDTO;
import com.example.Sweet_Dream.dto.response.FindIdResponseDTO;
import com.example.Sweet_Dream.dto.response.ResponseEmailDTO;
import com.example.Sweet_Dream.dto.response.ResponseSignUpDTO;
import com.example.Sweet_Dream.service.AccountService;
import com.example.Sweet_Dream.service.EmailService;
import com.example.Sweet_Dream.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;
    private final OtpService otpService;
    private final EmailService emailService;

    @PostMapping("/signup")
    public ResponseEntity<ResponseSignUpDTO> signUp(@RequestBody RequestSignUpDTO requestSignUpDTO) {
        ResponseSignUpDTO response = accountService.signUp(requestSignUpDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)  // 응답 타입을 명시적으로 설정
                .body(response);
    }

    // ✅ 아이디 찾기 - 인증 코드 요청
    @PostMapping("/id/authentication")
    public ResponseEntity<ResponseEmailDTO> sendOtpForId(@RequestBody RequestEmailDTO dto) {
        String otp = otpService.generateOtp();
        otpService.saveOtp(dto.getEmail(), otp);
        emailService.sendOtpEmail(dto.getEmail(), otp);
        return ResponseEntity.ok(new ResponseEmailDTO("OTP sent to " + dto.getEmail()));
    }

    // ✅ 아이디 찾기 - 인증 코드 검증
    @PostMapping("/id/authentication/verify")
    public ResponseEntity<String> verifyOtpForId(@RequestBody RequestOtpVerificationDTO requestDto) {
        if (otpService.verifyOtp(requestDto.getEmail(), requestDto.getOtp())) {
            String userId = accountService.getUserIdByEmail(requestDto.getEmail());
            return (userId != null)
                    ? ResponseEntity.ok("User ID: " + userId)
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired OTP");
    }


}
