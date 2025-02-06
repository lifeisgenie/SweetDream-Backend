package com.example.Sweet_Dream.dto.request;

import com.example.Sweet_Dream.entity.RoleName;
import lombok.Data;

@Data
public class RequestSignUpDTO {

    private String userId;
    private String username;
    private String email;
    private String password;
}