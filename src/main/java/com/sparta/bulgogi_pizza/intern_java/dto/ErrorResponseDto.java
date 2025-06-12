package com.sparta.bulgogi_pizza.intern_java.dto;

import lombok.Getter;

@Getter
public class ErrorResponseDto {

    private final ErrorDetails error;

    public ErrorResponseDto(String code, String message) {
        this.error = new ErrorDetails(code, message);
    }

    public static ErrorResponseDto of(String code, String message) {
        return new ErrorResponseDto(code, message);
    }

    @Getter
    private static class ErrorDetails {
        private final String code;
        private final String message;

        private ErrorDetails(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}