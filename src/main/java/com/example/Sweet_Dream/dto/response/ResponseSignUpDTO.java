package com.example.Sweet_Dream.dto.response;

public class ResponseSignUpDTO {
    private String userId;
    private String username;
    private String email;

    public ResponseSignUpDTO(String userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
    }
}
