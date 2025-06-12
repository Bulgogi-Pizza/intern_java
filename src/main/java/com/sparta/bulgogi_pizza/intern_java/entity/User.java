package com.sparta.bulgogi_pizza.intern_java.entity;

import lombok.Getter;

@Getter
public class User {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private UserRoleEnum role;
}
