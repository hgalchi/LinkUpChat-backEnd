package com.example.security.auth.controller;

import com.example.security.auth.entity.Group;
import com.example.security.auth.entity.Refresh;
import com.example.security.auth.entity.constant.RoleType;
import com.example.security.auth.entity.constant.TokenType;
import com.example.security.common.dto.UserCreateReqDto;
import com.example.security.common.utils.JwtUtil;
import com.example.security.user.entity.User;
import com.example.security.user.repository.GroupRepository;
import com.example.security.user.repository.RefreshRepository;
import com.example.security.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class AuthControllerTest {

    private MockMvc mvc;

    @Autowired
    WebApplicationContext ctx;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    RefreshRepository refreshRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtil jwtUtil;

    User user;

    final String USERNAME = "spring11";
    final String PASSWORD = "spring123";
    final String EMAIL = "spring11@naver.com";

    @BeforeEach
    public void setup(){
        mvc = MockMvcBuilders
                .webAppContextSetup(ctx)
                .apply(springSecurity())
                .alwaysDo(print())
                .build();

    }

    @Test
    @DisplayName("회원가입 성공")
    public void register_success() throws Exception {
        //given
        UserCreateReqDto dto = UserCreateReqDto.builder()
                .name(USERNAME)
                .password(PASSWORD)
                .email(EMAIL)
                .build();

        //when & then
        mvc.perform(post("/auth/signUp")
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원가입 실패_이메일 형식 오류")
    public void register_failWithValidation() throws Exception {
        //given
        String invalidEmail="wywuwid";
        UserCreateReqDto dto = UserCreateReqDto.builder()
                .name(USERNAME)
                .password(PASSWORD)
                .email(invalidEmail)
                .build();

        //when & then
        mvc.perform(post("/auth/signUp")
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("회원가입 실패_중복된 이메일")
    public void register_failWithDuplicatedEmail() throws Exception {
        //given
        registerUser();
        UserCreateReqDto dto = UserCreateReqDto.builder()
                .name(USERNAME)
                .password(PASSWORD)
                .email(EMAIL)
                .build();

        //when & then
        mvc.perform(post("/auth/signUp")
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("로그인")
    public void login_success() throws Exception {
        //given
        registerUser();

        //when & then
        mvc.perform(formLogin().user(EMAIL).password(PASSWORD))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("로그인 실패_잘못된 아이디")
    public void login_failWithWrongID() throws Exception {
        //given
        registerUser();

        //when & then
        mvc.perform(formLogin().user(EMAIL+"Wrong").password(PASSWORD))
                .andExpect(status().isNotFound());


    }
    @Test
    @DisplayName("사용자 탈퇴")
    @WithUserDetails(value=EMAIL,userDetailsServiceBeanName = "customUserDetailService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void deleteUser_success() throws Exception {
        //given
        Long userId = user.getId();

        //when
        mvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        User deleteUser = userRepository.findById(userId).orElse(null);
        //then
        Assertions.assertThat(deleteUser).isNotNull();
        Assertions.assertThat(deleteUser.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("액세스 토큰 재발급 성공")
    public void issueRefresh_success() throws Exception {
        // given
        User user = registerUser();
        String refreshToken = registerRefresh(user);

        // when
        MvcResult result = mvc.perform(post("/auth/reissue")
                        .cookie(new Cookie(TokenType.refreshToken.name(), refreshToken))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        String accessToken = result.getResponse().getHeader(TokenType.accessToken.name());
        assertThat(accessToken).isNotNull();
        assertThat(accessToken).isNotEmpty();

        Cookie refreshTokenCookie = result.getResponse().getCookie(TokenType.refreshToken.name());
        assertThat(refreshTokenCookie).isNotNull();
    }

    @Test
    @DisplayName("액세스 토큰 재발급 실패_리플레시 토큰을 찾을 수 없음")
    public void issueRefreshWithExpiredRefresh() throws Exception {

        //when & then
        mvc.perform(post("/auth/reissue")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    private User registerUser() {
        Group role = groupRepository.findByCode(RoleType.CUSTOMER.getRole()).get();
        User user = User.of(USERNAME, PASSWORD, EMAIL);
        user.addUserGroups(role);
        user.encodePassword(passwordEncoder.encode(PASSWORD));
        userRepository.save(user);
        return user;
    }

    private String registerRefresh(User user){
        String refreshToken = jwtUtil.createToken(EMAIL, Collections.singletonList("ROLE_USER"), TokenType.refreshToken.name());

        Refresh refresh = Refresh.builder()
                .refresh(refreshToken)
                .expiration(jwtUtil.refreshExpireTime())
                .user(user)
                .build();
        refreshRepository.save(refresh);
        return refreshToken;
    }

}
