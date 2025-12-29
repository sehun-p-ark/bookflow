package com.psh.bookflow.exception;

import com.psh.bookflow.dto.common.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ApiExceptionHandler {

    // AppException 발생 시 예외처리
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(
            AppException e, HttpServletRequest request) {

        ErrorCode errorCode = e.getErrorCode();

        ErrorResponse body = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(errorCode.getStatus().value())
                .error(errorCode.getStatus().name())
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(errorCode.getStatus()).body(body);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception e, HttpServletRequest request
    ) {
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(ErrorCode.INTERNAL_ERROR.getStatus().value())
                .error(ErrorCode.INTERNAL_ERROR.getStatus().name())
                .code(ErrorCode.INTERNAL_ERROR.name())
                .message(ErrorCode.INTERNAL_ERROR.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(ErrorCode.INTERNAL_ERROR.getStatus()).body(body);
    }
}
