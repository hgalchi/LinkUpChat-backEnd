package LinkUpTalk.user;

import LinkUpTalk.annotation.IntegrateTest;
import LinkUpTalk.annotation.UnitTest;
import LinkUpTalk.config.IntegrationTest;
import LinkUpTalk.util.TestUtil;
import LinkUpTalk.user.domain.User;
import LinkUpTalk.user.domain.repository.UserRepository;
import LinkUpTalk.user.presentation.dto.UserModifyReqDto;
import LinkUpTalk.user.presentation.dto.UserPasswordModifyReqDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class UserServiceIntegrationTest extends IntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext ctx;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TestUtil testUtil;

    private User user;
    final String USERNAME = "testUser";
    final String PASSWORD = "testUser1234";
    final String EMAIL = "testUser@test.com";

    @BeforeEach()
    public void setup() {
        this.mvc = MockMvcBuilders
                .webAppContextSetup(ctx)
                .apply(springSecurity())
                .alwaysDo(print())
                .build();

        user = testUtil.registerUser(USERNAME,EMAIL,PASSWORD);

    }

    @IntegrateTest
    @DisplayName("사용자 단건 조회 성공")
    @WithMockUser
    public void getsUser_suc() throws Exception {
        //given
        Long userId = user.getId();

        //when & then
        mvc.perform(get("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @IntegrateTest
    @DisplayName("사용자 단건 조회 실패_인증되지 않은 회원 접근")
    @WithAnonymousUser
    public void getUser_failWithUnAuthorized() throws Exception {
        //given
        Long userId = user.getId();

        //when & then
        mvc.perform(get("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @IntegrateTest
    @DisplayName("사용자 정보 수정")
    @WithUserDetails(value=EMAIL,userDetailsServiceBeanName = "userDetailsServiceImpl", setupBefore = TestExecutionEvent.TEST_EXECUTION)
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

    @IntegrateTest
    @DisplayName("사용자 정보 수정 실패_리소스 주인이 아님")
    @WithMockUser
    public void modifyUser_failWithForbidden() throws Exception {
        //given
        String newEmail = "wywudi@naver.com";
        UserModifyReqDto dto = UserModifyReqDto.builder()
                .name(USERNAME)
                .email(newEmail)
                .build();
        Long userId = user.getId();


        mvc.perform(post("/users/profile/{userId}", userId)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @IntegrateTest
    @DisplayName("사용자 비밀번호 수정")
    @WithUserDetails(value=EMAIL,userDetailsServiceBeanName = "userDetailsServiceImpl", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void modifyUserPassword() throws Exception {
        //given

        String newPassword = "testUser56789";
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

    @IntegrateTest
    @DisplayName("사용자 비밀번호 수정 실패_리소스 주인이 아님")
    @WithMockUser
    public void modifyUserPassword_failWithForbidden() throws Exception {
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
                .andExpect(status().isForbidden());

    }

}