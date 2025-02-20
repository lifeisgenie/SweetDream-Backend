package com.example.Sweet_Dream.dto.request;

import lombok.Data;

@Data
public class RequestOtpVerificationDTO {
    private String email;
    private String otp;
}