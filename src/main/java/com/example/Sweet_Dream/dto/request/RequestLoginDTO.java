package com.example.Sweet_Dream.dto.request;

import com.example.Sweet_Dream.entity.RoleName;
import lombok.Data;

@Data
public class RequestLoginDTO {
    private String userId;
    private String password;
}
