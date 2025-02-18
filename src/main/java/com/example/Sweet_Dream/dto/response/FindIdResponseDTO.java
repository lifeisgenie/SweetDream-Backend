package com.example.Sweet_Dream.dto.response;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class FindIdResponseDTO {
    private String resultCode; // 결과 코드
    private String resultMessage; // 결과 메시지
    private String userId; // 찾은 아이디
}
