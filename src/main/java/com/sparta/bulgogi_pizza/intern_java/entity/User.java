package com.sparta.bulgogi_pizza.intern_java.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
public class User {
    @Setter
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private UserRoleEnum role;

    public User(String username, String password, String nickname, UserRoleEnum role) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
    }

    public void updateRole(UserRoleEnum role) {
        this.role = role;
    }

}
