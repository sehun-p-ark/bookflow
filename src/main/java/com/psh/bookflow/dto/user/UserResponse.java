package com.psh.bookflow.dto.user;

import com.psh.bookflow.domain.User;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UserResponse {

    private final Long id;
    private final String email;
    private final String name;
    private final LocalDate birth;
    private final String phone;

    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.birth = user.getBirth();
        this.phone = user.getPhone();
    }
}
