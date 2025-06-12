package com.sparta.bulgogi_pizza.intern_java.dto;

import com.sparta.bulgogi_pizza.intern_java.entity.User;
import com.sparta.bulgogi_pizza.intern_java.entity.UserRoleEnum;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

@Getter
public class SignupResponseDto {

    private final String username;
    private final String nickname;
    private final List<RoleDto> roles;

    public SignupResponseDto(User user) {
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.roles = Collections.singletonList(new RoleDto(user.getRole()));
    }

    @Getter
    private static class RoleDto {
        private final String role;

        RoleDto(UserRoleEnum role) {
            this.role = role.name();
        }
    }
}