package com.psh.bookflow.dto.user;

import jakarta.validation.constraints.*;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class SignupRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 100)
    private String password;

    @NotBlank
    @Size(max = 30)
    private String name;

    @NotNull
    @Past
    private LocalDate birth;

    @NotBlank
    @Pattern(regexp = "^[0-9\\-+() ]{8,20}$")
    private String phone;
}

