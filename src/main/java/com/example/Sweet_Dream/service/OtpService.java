package com.example.Sweet_Dream.service;

import com.example.Sweet_Dream.entity.EmailVerification;
import com.example.Sweet_Dream.entity.VerificationStatus;
import com.example.Sweet_Dream.repository.EmailVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final EmailVerificationRepository verificationRepository;
    private final Random random = new Random();
    private static final int OTP_LENGTH = 6;
    private static final long OTP_EXPIRATION_MINUTES = 5;

    // OTP 생성
    public String generateOtp() {
        return String.format("%06d", random.nextInt(1000000));
    }

    // OTP 저장 (기존 OTP 삭제 후 새 OTP 저장)
    public void saveOtp(String email, String otp) {
        // 이미 존재하는 PENDING OTP 삭제
        verificationRepository.findByEmailAndStatus(email, VerificationStatus.PENDING)
                .ifPresent(verificationRepository::delete); // 기존 OTP 삭제

        // 새 OTP 저장
        EmailVerification verification = new EmailVerification(
                email, otp, LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES)
        );
        verificationRepository.save(verification);
    }

    // OTP 검증
    public boolean verifyOtp(String email, String otp) {
        // PENDING 상태인 OTP 찾기
        Optional<EmailVerification> verification = verificationRepository.findByEmailAndStatus(email, VerificationStatus.PENDING);

        // OTP 검증 로직
        if (verification.isPresent()) {
            EmailVerification emailVerification = verification.get();

            // OTP 값 비교
            if (emailVerification.getOtp().equals(otp)) {
                // OTP 만료 시간 체크
                if (emailVerification.getExpiredAt().isAfter(LocalDateTime.now())) {
                    // OTP 검증 성공 시 상태 변경
                    emailVerification.setStatus(VerificationStatus.VERIFIED);
                    verificationRepository.save(emailVerification);
                    return true;  // 검증 성공
                } else {
                    // OTP 만료 처리
                    emailVerification.setStatus(VerificationStatus.EXPIRED);
                    verificationRepository.save(emailVerification);
                    verificationRepository.delete(emailVerification);  // 만료된 OTP 삭제
                }
            }
        }
        return false;  // OTP 검증 실패
    }
}