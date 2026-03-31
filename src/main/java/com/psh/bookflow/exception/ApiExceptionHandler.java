package com.psh.bookflow.exception;

import com.psh.bookflow.dto.common.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException e,
            HttpServletRequest request
    ) {
        log.warn("Method not supported: method={}, uri={}, supported={}",
                request.getMethod(),
                request.getRequestURI(),
                e.getSupportedHttpMethods());

        ErrorResponse body = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(ErrorCode.METHOD_NOT_ALLOWED.getStatus().value())
                .error(ErrorCode.METHOD_NOT_ALLOWED.getStatus().name())
                .code(ErrorCode.METHOD_NOT_ALLOWED.name())
                .message(ErrorCode.METHOD_NOT_ALLOWED.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(ErrorCode.METHOD_NOT_ALLOWED.getStatus()).body(body);
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException e,
            HttpServletRequest request
    ) {
        log.warn("Argument type mismatch: method={}, uri={}, param={}, value={}",
                request.getMethod(),
                request.getRequestURI(),
                e.getName(),
                e.getValue());

        ErrorResponse body = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(ErrorCode.BAD_REQUEST.getStatus().value())
                .error(ErrorCode.BAD_REQUEST.getStatus().name())
                .code(ErrorCode.BAD_REQUEST.name())
                .message(ErrorCode.BAD_REQUEST.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(ErrorCode.BAD_REQUEST.getStatus()).body(body);
    }


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
        //
        log.error("Unhandled exception", e);

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
