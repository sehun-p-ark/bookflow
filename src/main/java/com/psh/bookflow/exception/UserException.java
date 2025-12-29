package com.psh.bookflow.exception;

public class UserException extends AppException{
    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }
}
