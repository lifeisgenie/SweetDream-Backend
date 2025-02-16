package com.example.Sweet_Dream.dto.response;

import lombok.Data;

@Data
public class ResponseSignInDTO {
    private String accessToken;
    private String refreshToken;
}
