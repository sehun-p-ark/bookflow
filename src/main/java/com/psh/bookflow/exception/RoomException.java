package com.psh.bookflow.exception;

public class RoomException extends AppException {
    public RoomException(ErrorCode errorCode) {
        super(errorCode);
    }
}
