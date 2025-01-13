package com.example.LinkUpTalk.user.controller;

import com.example.LinkUpTalk.Util.TestUtil;
import LinkUpTalk.user.dto.UserModifyReqDto;
import LinkUpTalk.user.dto.UserPasswordModifyReqDto;
import LinkUpTalk.user.domain.User;
import LinkUpTalk.user.infrastructor.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    private WebApplicationContext ctx;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TestUtil testUtil;

    private User user;
    final String USERNAME = "spring11";
    final String PASSWORD = "spring123";
    final String EMAIL = "spring11@naver.com";

    @BeforeEach()
    public void setup() {
        this.mvc = MockMvcBuilders
                .webAppContextSetup(ctx)
                .alwaysDo(print())
                .build();
        user = testUtil.registerUser();
    }

    @Test
    @DisplayName("사용자 단건 조회")
    @WithMockUser
    public void getsUser() throws Exception {
        //given
        Long userId = user.getId();

        //when & then
        mvc.perform(get("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("사용자 단건 조회_인증되지 않은 회원 접근")
    @WithAnonymousUser
    public void getUser_fail() throws Exception {
        //given
        Long userId = user.getId();

        //when & then
        mvc.perform(get("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("사용자 정보 수정")
    @WithUserDetails(value=EMAIL,userDetailsServiceBeanName = "customUserDetailService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void modifyUser() throws Exception {
        //given
        String newEmail = "wywudi@naver.com";
        UserModifyReqDto dto = UserModifyReqDto.builder()
                .name(USERNAME)
                .email(newEmail)
                .build();
        Long userId = user.getId();

        //when
        mvc.perform(post("/users/profile/{userId}",userId)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
        User modifyUser = userRepository.findByEmail(newEmail).orElse(null);
        assertThat(modifyUser).isNotNull();
        assertThat(modifyUser.getEmail()).isEqualTo(newEmail);
        assertThat(modifyUser.getName()).isEqualTo(USERNAME);
    }

    @Test
    @DisplayName("사용자 비밀번호 수정")
    @WithUserDetails(value=EMAIL,userDetailsServiceBeanName = "customUserDetailService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void modifyUserPassword() throws Exception {
        //given
        String newPassword = "12345678";
        UserPasswordModifyReqDto dto = UserPasswordModifyReqDto.builder()
                .password(PASSWORD)
                .newPassword(newPassword)
                .build();
        Long userId = user.getId();

        //when
        mvc.perform(post("/users/profile/password/{userId}",userId)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
        User modifyUser = userRepository.findByEmail(EMAIL).orElse(null);
        assertThat(modifyUser).isNotNull();
        assertThat(passwordEncoder.matches(newPassword, modifyUser.getPassword())).isTrue();
    }

}