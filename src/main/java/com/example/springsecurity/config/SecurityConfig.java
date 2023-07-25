package com.example.springsecurity.config;

import com.example.springsecurity.security.JwtAuthenticationFilter;
import com.example.springsecurity.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

//spring security 전반적 설정
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtProvider provider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                //email, password 문자열을 Base64 로 인코딩하여 전달하는 구조
                .httpBasic().disable()
                //쿠키 기반이 아닌 jwt 기반이므로 사용하지 않음
                .csrf().disable()
                //cors설정
                .cors(c -> {
                    CorsConfigurationSource source = request -> {
                        //Cors 허용 패턴
                        CorsConfiguration corsConfiguration = new CorsConfiguration();
                        corsConfiguration.setAllowedOrigins(List.of("*"));
                        corsConfiguration.setAllowedMethods(List.of("*"));
                        return corsConfiguration;
                    };
                    c.configurationSource(source);
                })
                //spring security 세션 정책 : 세션을 사용하지 않음
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeRequests()

                // 회원가입, 로그인, 스웨거
                .antMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**").permitAll()
                .antMatchers("/login", "/register").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/user/**").hasRole("USER")
                .anyRequest().authenticated()

                .and()
                //jwt 인증 필터 적용
                //기본인증필터인 UsernamePasswordAuthenticationFilter의 앞에서 인증이 이뤄지면(SecurityContextHolder에 인증정보가 추가되면) AuthenticationFilter에서 인증 다음 흐름으로 넘어가는 방식으로 이해했다.
                .addFilterBefore(new JwtAuthenticationFilter(provider), UsernamePasswordAuthenticationFilter.class)

                //에러핸들링
                .exceptionHandling()
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException, IOException {
                        // 권한 문제가 발생했을 때 이 부분을 호출한다.
                        response.setStatus(403);
                        response.setCharacterEncoding("utf-8");
                        response.setContentType("text/html; charset=UTF-8");
                        response.getWriter().write("권한이 없는 사용자입니다.");
                    }
                })
//                .authenticationEntryPoint(new AuthenticationEntryPoint() {
//                    @Override
//                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
//                        // 인증문제가 발생했을 때 이 부분을 호출한다.
//                        response.setStatus(401);
//                        response.setCharacterEncoding("utf-8");
//                        response.setContentType("text/html; charset=UTF-8");
//                        response.getWriter().write("인증되지 않은 사용자입니다.");
//                    }
//                })
        ;

        return http.build();
    }

    //PasswordEncoder를 createDelegatingPasswordEncoder()로 설정하면
    //{noop} asdf!@#asdfvz!@#... 처럼 password의 앞에 Encoding 방식이 붙은채로 저장되어 암호화 방식을 지정하여 저장할 수 있다.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
