package com.sparta.bulgogi_pizza.intern_java.service;

import com.sparta.bulgogi_pizza.intern_java.dto.GrantAdminRoleResponseDto;
import com.sparta.bulgogi_pizza.intern_java.entity.User;
import com.sparta.bulgogi_pizza.intern_java.entity.UserRoleEnum;
import com.sparta.bulgogi_pizza.intern_java.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public GrantAdminRoleResponseDto grantAdminRole(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
            new IllegalArgumentException("해당 ID의 사용자를 찾을 수 없습니다.")
        );

        user.updateRole(UserRoleEnum.ADMIN);

        userRepository.save(user);

        return new GrantAdminRoleResponseDto(user);
    }
}
