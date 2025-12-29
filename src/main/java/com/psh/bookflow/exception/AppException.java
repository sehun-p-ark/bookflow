package com.psh.bookflow.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
    // ErrorCode에서 어떤 Error인지
    private final ErrorCode errorCode;

    // 생성자 : ErrorCode를 받아서 메세지도 같이 생성
    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // RuntimeException 메세지로 사용
        this.errorCode = errorCode;
    }
}
