package com.example.Sweet_Dream.dto.request;


import lombok.*;

@Setter
@Getter
@RequiredArgsConstructor
public class RequestUpdateMyPageDTO {

    private String email;
    private String username;
    private String image;

}
