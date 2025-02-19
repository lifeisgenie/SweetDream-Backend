package com.example.Sweet_Dream.controller;

import com.example.Sweet_Dream.service.EmailService;
import com.example.Sweet_Dream.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class EmailVerificationController {
    private final OtpService otpService;
    private final EmailService emailService;

    // OTP 발송 요청
    @PostMapping("/id/authentication")
    public ResponseEntity<String> sendOtpForId(@RequestParam String email) {
        String otp = otpService.generateOtp();  // OTP 생성
        otpService.saveOtp(email, otp);  // OTP 저장
        emailService.sendOtpEmail(email, otp);  // 이메일 발송
        return ResponseEntity.ok("OTP sent to " + email);
    }

    // OTP 검증 요청
    @PostMapping("/id/authentication/verify")
    public ResponseEntity<String> verifyOtpForId(@RequestParam String email, @RequestParam String otp) {
        boolean isValid = otpService.verifyOtp(email, otp);  // OTP 검증
        if (isValid) {
            return ResponseEntity.ok("OTP verified successfully");  // 성공
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired OTP");  // 실패
    }
}

