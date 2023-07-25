package com.example.springsecurity.dto;

import com.example.springsecurity.security.Authority;
import com.example.springsecurity.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class SignResponse {
    private Long id;
    private String email;
    private String username;
    private List<Authority> roles = new ArrayList<>();
    private String token;

    public SignResponse(Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.username = member.getUsername();
        this.roles = member.getRoles();
    }
}
