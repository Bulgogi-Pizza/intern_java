package com.sparta.bulgogi_pizza.intern_java.service;

import com.sparta.bulgogi_pizza.intern_java.dto.SignupRequestDto;
import com.sparta.bulgogi_pizza.intern_java.dto.SignupResponseDto;
import com.sparta.bulgogi_pizza.intern_java.entity.User;
import com.sparta.bulgogi_pizza.intern_java.entity.UserRoleEnum;
import com.sparta.bulgogi_pizza.intern_java.exception.UserAlreadyExistsException;
import com.sparta.bulgogi_pizza.intern_java.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SignupResponseDto signup(SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword()); // 비밀번호 암호화
        String nickname = requestDto.getNickname();

        Optional<User> checkUsername = userRepository.findByUsername(username);
        if (checkUsername.isPresent()) {
            throw new UserAlreadyExistsException("이미 가입된 사용자입니다.");
        }

        UserRoleEnum role = UserRoleEnum.USER;

        User user = new User(username, password, nickname, role);
        User savedUser = userRepository.save(user);

        return new SignupResponseDto(savedUser);
    }
}
