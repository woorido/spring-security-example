package com.example.springsecurity.member;

import com.example.springsecurity.member.dto.SignRequest;
import com.example.springsecurity.member.dto.SignResponse;
import com.example.springsecurity.security.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@Transactional
@AllArgsConstructor
public class SignService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder encoder;
    private final JwtProvider jwtProvider;

    public SignResponse login(SignRequest request) throws Exception {
        Member member = memberRepository.findByEmail(request.getEmail()).orElseThrow(() -> {
            throw new BadCredentialsException("잘못된 계정정보입니다.");
        });

        if (!encoder.matches(request.getPassword(), encoder.encode(member.getPassword()))) {
            throw new BadCredentialsException("잘못된 계정정보입니다.");
        }

        return SignResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .username(member.getUsername())
                .roles(member.getRoles())
                .token(jwtProvider.createToken(member.getEmail(), member.getRoles()))
                .build();
    }

    public boolean register(SignRequest request) throws Exception {
        try {
            Member member = Member.builder()
                    .email(request.getEmail())
                    .password(request.getPassword())
                    .username(request.getUsername())
                    .build();

            member.changeRoles(Collections.singletonList(Authority.builder().name("ROLE_USER").build()));

            memberRepository.save(member);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("잘못된 요청입니다.");
        }
        return true;
    }

    public SignResponse getMember(String email) throws Exception {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new Exception("계정을 찾을 수 없다"));
        return new SignResponse(member);
    }
}
