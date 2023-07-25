package com.example.springsecurity.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString(of = {"id", "email", "password", "username"})
public class SignRequest {
    private Long id;
    private String email;
    private String password;
    private String username;

    public SignRequest(Long id, String email, String password, String username) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.username = username;
    }
}
