package com.psh.bookflow.dto.user;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String email;    // 로그인 아이디
    private String password; // 입력한 평문 비밀번호
}
