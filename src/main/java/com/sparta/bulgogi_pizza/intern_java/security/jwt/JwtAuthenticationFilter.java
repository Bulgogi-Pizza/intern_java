package com.sparta.bulgogi_pizza.intern_java.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.bulgogi_pizza.intern_java.dto.ErrorResponseDto;
import com.sparta.bulgogi_pizza.intern_java.dto.LoginRequestDto;
import com.sparta.bulgogi_pizza.intern_java.dto.LoginResponseDto;
import com.sparta.bulgogi_pizza.intern_java.entity.UserRoleEnum;
import com.sparta.bulgogi_pizza.intern_java.security.UserDetailsImpl;
import jakarta.security.auth.message.AuthException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j(topic = "JwtAuthenticationFilter")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Builder
    public JwtAuthenticationFilter(
        JwtUtil jwtUtil
    ) {
        setFilterProcessesUrl("/login");
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Authentication attemptAuthentication(
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        try {
            log.info("로그인 시도, {}", request.getRequestURI());

            LoginRequestDto requestDto = new ObjectMapper()
                .readValue(
                    request.getInputStream(),
                    LoginRequestDto.class
                );

            return getAuthenticationManager()
                .authenticate(
                    new UsernamePasswordAuthenticationToken(
                        requestDto.getUsername(),
                        requestDto.getPassword(),
                        null
                    )
                );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain,
        Authentication auth
    ) throws IOException {
        log.info("로그인 성공, {}", auth.getName());

        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        String username = userDetails.getUsername();
        UserRoleEnum role = userDetails.getUser().getRole();

        String token = jwtUtil.createToken(username, role);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json; charset=UTF-8");

        LoginResponseDto responseDto = new LoginResponseDto(token);
        response.getWriter().write(objectMapper.writeValueAsString(responseDto));
    }

    @Override
    protected void unsuccessfulAuthentication(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException failed
    ) throws IOException {
        log.info("로그인 실페");

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=UTF-8");

        ErrorResponseDto errorResponse = ErrorResponseDto.of("INVALID_CREDENTIALS", "아이디 또는 비밀번호가 올바르지 않습니다.");
        String result = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(result);
    }

}
