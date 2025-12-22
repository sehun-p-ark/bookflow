package com.psh.bookflow.dto.user;

import com.psh.bookflow.domain.User;
import lombok.Getter;

@Getter
public class UserResponse {

    private Long id;
    private String email;
    private String name;

    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
    }
}
