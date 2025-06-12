package com.sparta.bulgogi_pizza.intern_java.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDto {

    private String token;

    public LoginResponseDto(String token) {
        this.token = token;
    }
}
