package com.example.Sweet_Dream.dto.response;

import lombok.Data;

@Data
public class ResponseEmailDTO {
    private String message;

    public ResponseEmailDTO(String message) {
        this.message = message;
    }
}