package com.example.springsecurity.security;

import com.example.springsecurity.member.Authority;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

//jwt 생성 및 검증
@RequiredArgsConstructor
@Component
public class JwtProvider {

    @Value("${jwt.secret.key}")
    private String key;

    private Key secretKey;

    // 만료시간 : 1Hour
    private final long exp = 1000L * 60 * 60;

    private final JpaUserDetailsService service;

    @PostConstruct
    protected void init() {
        secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }

    //token 생성
    public String createToken(String email, List<Authority> roles) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("roles", roles);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + exp))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    //권한정보 획득
    //spring security 인증 과정에서 권한 확인을 위한 기능
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = service.loadUserByUsername(this.getEmail(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    //token 에 담겨있는 member email 조회
    private String getEmail(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getSubject();
    }

    //Authorization Header 통해 인증
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    //token 검증
    public boolean validateToken(String token) {
        try {
            // Bearer 검증
            if (!token.substring(0, "BEARER ".length()).equalsIgnoreCase("BEARER")) {
                return false;
            } else {
                token = token.split(" ")[1].trim();
            }
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);

            //만료되었을 시 false
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
