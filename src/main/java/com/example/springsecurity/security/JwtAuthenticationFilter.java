package com.example.springsecurity.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//Jwt 를 검증하기 위한 filter 형태의 클래스
//스프링 부트에서 필터란 controller 역할을 하는 메소드에게 요청이 전달되기 전에 행동을 하는 기능
//security 는 기본적으로 서버 자원인 session 을 통하여 로그인 여부를 검증하는데,
//이러한 검증 방법을 JWT 로 바꾸어 주기 위해서는 JWT 값을 통해 동작하는 필터클래스를 만들어줘야 한다
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    protected final JwtProvider provider;

    public JwtAuthenticationFilter(JwtProvider provider) {
        this.provider = provider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = provider.resolveToken(request);
        //유효한 토큰인지 확인
        if (StringUtils.hasText(token) && provider.validateToken(token)) {
            //토큰이 유효하면 토큰으로부터 유저 정보를 받아옵니다.
            Authentication authentication = provider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
