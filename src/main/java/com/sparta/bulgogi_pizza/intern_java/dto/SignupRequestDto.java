package com.sparta.bulgogi_pizza.intern_java.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDto {

    @NotBlank(message = "사용자 이름은 필수 입력 항목입니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    private String password;

    @NotBlank(message = "닉네임은 필수 입력 항목입니다.")
    private String nickname;
}