package com.psh.bookflow.exception;

// 예약 도메인에서만 사용하는 예외
public class ReservationException extends AppException {

    public ReservationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
