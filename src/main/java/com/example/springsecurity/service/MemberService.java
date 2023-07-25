package com.example.springsecurity.service;

import com.example.springsecurity.entity.Member;
import com.example.springsecurity.dto.SignResponse;
import com.example.springsecurity.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public SignResponse getMember(String email) throws Exception {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new Exception("계정을 찾을 수 없다"));
        return new SignResponse(member);
    }
}
