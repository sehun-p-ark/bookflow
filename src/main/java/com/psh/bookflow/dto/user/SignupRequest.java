package com.psh.bookflow.dto.user;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class SignupRequest {
    private String email;
    private String password;
    private String name;
    private LocalDate birth;
    private String phone;
}

