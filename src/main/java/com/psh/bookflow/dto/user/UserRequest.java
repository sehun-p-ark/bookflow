package com.psh.bookflow.dto.user;

import lombok.Getter;

@Getter
public class UserRequest {
    private String email;
    private String password;
    private String name;
}

