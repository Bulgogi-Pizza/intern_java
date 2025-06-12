package com.sparta.bulgogi_pizza.intern_java.security.jwt;

import com.sparta.bulgogi_pizza.intern_java.entity.UserRoleEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_ROLE_KEY = "role";

    @Value("${jwt.secret.key}")
    private String secretKey;
    @Value("${jwt.token.expiration}")
    private long tokenExpiration;
    private Key key;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String createToken(String username, UserRoleEnum role) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + tokenExpiration);

        return BEARER_PREFIX +
            Jwts.builder()
                .setSubject(username)
                .claim(AUTHORIZATION_ROLE_KEY, role)
                .setExpiration(expireDate)
                .setIssuedAt(now)
                .signWith(key)
                .compact();
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("유효하지 않은 인증 정보 입니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 인증 정보 입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 인증 정보 입니다.");
        } catch (IllegalArgumentException e) {
            log.error("잘못된 인증 정보 입니다.");
        }
        return false;
    }

    public Claims getUserInfoFromToken(String token) {
        return Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}