package com.psh.bookflow.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // ===== User ====
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    USER_EMAIL_DUPLICATED(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    USER_PASSWORD_MISMATCH(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),

    // ===== Room =====
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 객실입니다."),
    ROOM_NOT_AVAILABLE(HttpStatus.CONFLICT, "예약 가능한 객실 상태가 아닙니다."),

    // ===== Reservation - create =====
    RESERVATION_DATE_INVALID(HttpStatus.BAD_REQUEST, "체크인/체크아웃 날짜가 올바르지 않습니다."),
    RESERVATION_OVERLAPPED(HttpStatus.CONFLICT, "해당 기간에 이미 예약이 존재합니다."),

    // ===== Reservation - common =====
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "예약을 찾을 수 없습니다."),
    RESERVATION_INVALID_STATUS(HttpStatus.CONFLICT, "현재 예약 상태에서는 수행할 수 없는 요청입니다."),
    RESERVATION_COMPLETED_CANNOT_CANCEL(HttpStatus.CONFLICT, "완료된 예약은 취소할 수 없습니다."),

    // ===== Common =====
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
