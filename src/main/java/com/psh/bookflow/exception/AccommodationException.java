package com.psh.bookflow.exception;

public class AccommodationException extends AppException {
    public AccommodationException(ErrorCode errorCode) {
        super(errorCode);
    }
}