package com.sparta.bulgogi_pizza.intern_java.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.bulgogi_pizza.intern_java.dto.LoginRequestDto;
import com.sparta.bulgogi_pizza.intern_java.dto.SignupRequestDto;
import com.sparta.bulgogi_pizza.intern_java.entity.User;
import com.sparta.bulgogi_pizza.intern_java.entity.UserRoleEnum;
import com.sparta.bulgogi_pizza.intern_java.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("인증/인가 API 테스트")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private User testAdmin;

    @BeforeEach
    void setUp() {
        userRepository.clearStore();

        testUser = new User("testuser", passwordEncoder.encode("password"), "testnickname", UserRoleEnum.USER);
        userRepository.save(testUser);

        testAdmin = new User("adminuser", passwordEncoder.encode("password"), "adminnickname", UserRoleEnum.ADMIN);
        userRepository.save(testAdmin);
    }

    @Nested
    @DisplayName("회원가입 테스트")
    class SignupTest {
        @Test
        @DisplayName("회원가입 성공")
        void signup_success() throws Exception {
            // given
            SignupRequestDto requestDto = new SignupRequestDto();
            requestDto.setUsername("newUser");
            requestDto.setPassword("newPassword");
            requestDto.setNickname("newNickname");

            String jsonRequest = objectMapper.writeValueAsString(requestDto);

            // when & then
            mockMvc.perform(post("/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newUser"))
                .andExpect(jsonPath("$.nickname").value("newNickname"))
                .andExpect(jsonPath("$.roles[0].role").value("USER"))
                .andDo(print());
        }

        @Test
        @DisplayName("회원가입 실패 - 중복된 사용자 이름")
        void signup_fail_duplicate_username() throws Exception {
            // given
            SignupRequestDto requestDto = new SignupRequestDto();
            requestDto.setUsername("testuser"); // 이미 존재하는 사용자 이름
            requestDto.setPassword("password");
            requestDto.setNickname("nickname");

            String jsonRequest = objectMapper.writeValueAsString(requestDto);

            // when & then
            mockMvc.perform(post("/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest))
                .andExpect(status().isConflict()) // 409 Conflict
                .andExpect(jsonPath("$.error.code").value("USER_ALREADY_EXISTS"))
                .andDo(print());
        }
    }

    @Nested
    @DisplayName("로그인 테스트")
    class LoginTest {
        @Test
        @DisplayName("로그인 성공")
        void login_success() throws Exception {
            // given
            LoginRequestDto requestDto = new LoginRequestDto();
            requestDto.setUsername("testuser");
            requestDto.setPassword("password");

            String jsonRequest = objectMapper.writeValueAsString(requestDto);

            // when & then
            mockMvc.perform(post("/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists()) // 응답에 token 필드가 있는지 확인
                .andDo(print());
        }

        @Test
        @DisplayName("로그인 실패 - 잘못된 비밀번호")
        void login_fail_wrong_password() throws Exception {
            // given
            LoginRequestDto requestDto = new LoginRequestDto();
            requestDto.setUsername("testuser");
            requestDto.setPassword("wrongpassword");

            String jsonRequest = objectMapper.writeValueAsString(requestDto);

            // when & then
            mockMvc.perform(post("/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest))
                .andExpect(status().isUnauthorized()) // 401 Unauthorized
                .andExpect(jsonPath("$.error.code").value("INVALID_CREDENTIALS"))
                .andDo(print());
        }
    }

    @Nested
    @DisplayName("관리자 권한 부여 테스트")
    class AdminRoleGrantTest {

        private String adminToken;
        private String userToken;

        // 각 테스트 전에 관리자와 일반 사용자의 토큰을 미리 발급받음
        @BeforeEach
        void getTokens() throws Exception {
            adminToken = getToken("adminuser", "password");
            userToken = getToken("testuser", "password");
        }

        @Test
        @DisplayName("권한 부여 성공 - 관리자 요청")
        void grant_role_success_by_admin() throws Exception {
            // when & then
            mockMvc.perform(patch("/admin/users/{userId}/roles", testUser.getId())
                    .header("Authorization", adminToken)) // 관리자 토큰 사용
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(testUser.getUsername()))
                .andExpect(jsonPath("$.roles[0].role").value("ADMIN"))
                .andDo(print());
        }

        @Test
        @DisplayName("권한 부여 실패 - 일반 사용자 요청")
        void grant_role_fail_by_user() throws Exception {
            // when & then
            mockMvc.perform(patch("/admin/users/{userId}/roles", testAdmin.getId())
                    .header("Authorization", userToken)) // 일반 사용자 토큰 사용
                .andExpect(status().isForbidden()) // 403 Forbidden
                .andExpect(jsonPath("$.error.code").value("ACCESS_DENIED"))
                .andDo(print());
        }

        @Test
        @DisplayName("권한 부여 실패 - 존재하지 않는 사용자")
        void grant_role_fail_user_not_found() throws Exception {
            // given
            long nonExistentUserId = 999L;

            // when & then
            mockMvc.perform(patch("/admin/users/{userId}/roles", nonExistentUserId)
                    .header("Authorization", adminToken)) // 관리자 토큰 사용
                .andExpect(status().isBadRequest()) // 400 Bad Request
                .andExpect(jsonPath("$.error.code").value("INVALID_ARGUMENT"))
                .andDo(print());
        }

        // 로그인 요청을 보내고 토큰을 반환하는 헬퍼 메서드
        private String getToken(String username, String password) throws Exception {
            LoginRequestDto requestDto = new LoginRequestDto();
            requestDto.setUsername(username);
            requestDto.setPassword(password);

            MvcResult result = mockMvc.perform(post("/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                .andReturn();

            // 응답 본문에서 "token" 필드의 값을 추출
            String responseBody = result.getResponse().getContentAsString();
            return objectMapper.readTree(responseBody).get("token").asText();
        }
    }
}