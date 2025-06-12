package com.sparta.bulgogi_pizza.intern_java.security.jwt;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.bulgogi_pizza.intern_java.dto.ErrorResponseDto;
import com.sparta.bulgogi_pizza.intern_java.security.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j(topic = "JwtAuthorizationFilter")
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String[] excludePath = {
            "/signup",
            "/login",
            "/swagger-ui",
            "/v3/api-docs"
        };
        String path = request.getRequestURI();

        return Arrays.stream(excludePath).anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        log.info("JWT Authorization Filter");
        String token = jwtUtil.resolveToken(request);

        if (StringUtils.hasText(token)) {
            if (!jwtUtil.validateToken(token)) {
                log.error("유효하지 않은 토큰입니다.");
                sendErrorResponse(response, "INVALID_TOKEN", "유효하지 않은 인증 토큰입니다.");
                return;
            }
            Claims claims = jwtUtil.getUserInfoFromToken(token);
            try {
                setAuthentication(claims.getSubject());
            } catch (Exception e) {
                log.error("인증 에러가 발생했습니다. {}", e.getMessage());
                sendErrorResponse(response, "AUTHENTICATION_FAILED", "인증에 실패했습니다.");
            }
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    private void sendErrorResponse(
        HttpServletResponse response,
        String code,
        String message
    )
        throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(code, message);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponseDto));
    }
}
