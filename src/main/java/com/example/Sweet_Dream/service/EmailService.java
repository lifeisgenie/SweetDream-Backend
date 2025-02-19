package com.example.Sweet_Dream.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    // OTP 이메일 발송
    public void sendOtpEmail(String email, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("[회원관리 시스템] 인증 코드 안내");
            message.setText("인증 코드: " + otp + " (5분 내 입력)");

            mailSender.send(message);
        } catch (MailException e) {
            throw new RuntimeException("이메일 전송 실패: " + e.getMessage());
        }
    }
}