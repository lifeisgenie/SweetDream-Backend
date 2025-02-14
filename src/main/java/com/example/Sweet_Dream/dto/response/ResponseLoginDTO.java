package com.example.Sweet_Dream.dto.response;

import lombok.Data;

@Data
public class ResponseLoginDTO {
    private String accessToken;
    private String refreshToken;
}
