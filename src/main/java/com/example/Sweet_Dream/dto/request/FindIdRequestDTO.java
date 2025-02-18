package com.example.Sweet_Dream.dto.request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindIdRequestDTO {
    private String username;
    private String email;
}
