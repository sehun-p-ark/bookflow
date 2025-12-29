package com.psh.bookflow.dto.common;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResponse {
    private final LocalDateTime timestamp; //에러 발생 시간
    private final int status; // HTTP sㄲtate (400, 404, 500...)
    private final String error; // HTTP state name (Bad_Request, Not Found)..)
    private final String code; // 서버에서 정의한 에러 코드 RESERVATION_STATE_INVALID
    private final String message; // 사용자에게 보여줄 메세지
    private final String path; // 요청 URI
}
