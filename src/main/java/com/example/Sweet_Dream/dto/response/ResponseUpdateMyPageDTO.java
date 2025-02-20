package com.example.Sweet_Dream.dto.response;


import com.example.Sweet_Dream.entity.User;
import lombok.*;

@Getter
public class ResponseUpdateMyPageDTO {

    private final String email;
    private final String username;
    private final String image;

    public ResponseUpdateMyPageDTO(User user) {
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.image = user.getImage();
    }


}
