package com.sparta.bulgogi_pizza.intern_java.controller;

import com.sparta.bulgogi_pizza.intern_java.dto.LoginRequestDto;
import com.sparta.bulgogi_pizza.intern_java.dto.SignupRequestDto;
import com.sparta.bulgogi_pizza.intern_java.dto.SignupResponseDto;
import com.sparta.bulgogi_pizza.intern_java.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j(topic = "UserController")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponse(responseCode = "200", description = "회원가입 성공")
    @ApiResponse(responseCode = "409", description = "이미 가입된 사용자 (USER_ALREADY_EXISTS)")
    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(@RequestBody @Valid SignupRequestDto requestDto) {
        log.info("회원가입 시도: {}", requestDto.getUsername());

        SignupResponseDto responseDto = userService.signup(requestDto);

        log.info("회원가입 성공: {}", requestDto.getUsername());
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "로그인", description = "사용자 로그인을 하고 JWT를 발급합니다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공 및 토큰 발급")
    @ApiResponse(responseCode = "401", description = "아이디 또는 비밀번호 불일치 (INVALID_CREDENTIALS)")
    @PostMapping("/login")
    public void loginForSwagger(@RequestBody LoginRequestDto requestDto) {
        throw new UnsupportedOperationException("이 메서드는 Swagger 문서화를 위한 것입니다.");
    }
}