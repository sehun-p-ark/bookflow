package com.psh.bookflow.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class LoginRequest {
    @NotBlank
    @Email
    private String email;    // 로그인 아이디

    @NotBlank
    @Size(min = 8, max = 100)
    private String password; // 입력한 평문 비밀번호
}
