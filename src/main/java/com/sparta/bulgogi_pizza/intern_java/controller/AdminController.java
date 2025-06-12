package com.sparta.bulgogi_pizza.intern_java.controller;

import com.sparta.bulgogi_pizza.intern_java.dto.GrantAdminRoleResponseDto;
import com.sparta.bulgogi_pizza.intern_java.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @Secured("ROLE_ADMIN")
    @Operation(summary = "관리자 권한 부여", description = "특정 사용자에게 관리자 권한을 부여합니다. 관리자 토큰이 필요합니다.")
    @Parameter(name = "userId", description = "권한을 부여할 사용자의 ID", required = true)
    @ApiResponse(responseCode = "200", description = "권한 부여 성공")
    @ApiResponse(responseCode = "403", description = "접근 권한 없음 (ACCESS_DENIED)")
    @ApiResponse(responseCode = "400", description = "존재하지 않는 사용자 (INVALID_ARGUMENT)")
    @PatchMapping("/users/{userId}/roles")
    public ResponseEntity<GrantAdminRoleResponseDto> grantAdminRole(@PathVariable Long userId) {
        GrantAdminRoleResponseDto responseDto = adminService.grantAdminRole(userId);
        return ResponseEntity.ok(responseDto);
    }

}
