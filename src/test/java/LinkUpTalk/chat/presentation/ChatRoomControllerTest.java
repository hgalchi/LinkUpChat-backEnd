package LinkUpTalk.chat.presentation;

import LinkUpTalk.annotation.UnitTest;
import LinkUpTalk.chat.application.ChatRoomService;
import LinkUpTalk.config.TestSecurityConfig;
import LinkUpTalk.config.TestWebSocketConfig;
import LinkUpTalk.chat.presentation.dto.ChatRoomCreateReqDto;
import LinkUpTalk.chat.presentation.dto.ChatRoomModifyReqDto;
import LinkUpTalk.chat.presentation.dto.ChatroomGetResDto;
import LinkUpTalk.common.response.ResponseCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(value = ChatRoomController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = OncePerRequestFilter.class)
        })
@Import({TestWebSocketConfig.class, TestSecurityConfig.class})
class ChatRoomControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ChatRoomService chatRoomService;

    @UnitTest
    @WithMockUser
    @DisplayName("유효한 그룹 채팅방 생성 요청 시, 채팅방 생성 성공한다.")
    public void createGroupChatRoom() throws Exception {
        //given
        ChatRoomCreateReqDto reqDto = ChatRoomCreateReqDto.builder().name("Test Room").userId(1L).capacity(10).build();
        String requestBody = new ObjectMapper().writeValueAsString(reqDto);

        //when & then
        mvc.perform(post("/chat/rooms/group")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResponseCode.CHATROOM_CREATE.getMessage()));

        verify(chatRoomService, times(1)).createGroup(any(ChatRoomCreateReqDto.class));

    }

    @UnitTest
    @WithMockUser
    @DisplayName("유효한 개인 채팅방 생성 요청 시, 채팅방 생성 성공한다.한다.")
    public void createDmChatRoom() throws Exception {

        //when & then
        mvc.perform(post("/chat/rooms/dm/{receiverId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResponseCode.CHATROOM_CREATE.getMessage()));

        verify(chatRoomService, times(1)).createDm(anyLong(), anyString());
    }


    @UnitTest
    @WithMockUser
    @DisplayName("유효한 채팅방 id로 단일 조회 시, 채팅방 정보 반환 성공한다.")
    void getChatRoom_suc() throws Exception {
        //given
        ChatroomGetResDto dto = ChatroomGetResDto.builder().name("Test ChatRoom").capacity(10).build();
        when(chatRoomService.getChatRoom(anyLong())).thenReturn(dto);

        //when & then
        mvc.perform(get("/chat/rooms/{roomId}", anyLong())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResponseCode.CHATROOM_READ.getMessage()))
                .andExpect(jsonPath("$.data.name").value("Test ChatRoom"))
                .andExpect(jsonPath("$.data.capacity").value(10));

        verify(chatRoomService, times(1)).getChatRoom(anyLong());
    }

    @UnitTest
    @WithMockUser
    @DisplayName("키워드 검색 조건으로 채팅방 리스트 조회 시, 검색 결과 반환 성공한다.")
    void getChatRooms_suc() throws Exception {
        //given
        List<ChatroomGetResDto> response = List.of(
                ChatroomGetResDto.builder().name("room 1").id(1L).participantCount(10).build(),
                ChatroomGetResDto.builder().name("room 2").id(2L).participantCount(10).build()
        );

        when(chatRoomService.getChatRooms(any(Pageable.class), anyString())).thenReturn(response);

        // When & Then
        mvc.perform(get("/chat/rooms")
                        .param("keyWord", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResponseCode.CHATROOM_LIST_READ.getMessage()))
                .andExpect(jsonPath("$.data[0].name").value("room 1"))
                .andExpect(jsonPath("$.data[1].name").value("room 2"));
    }

    @UnitTest
    @WithMockUser
    @DisplayName("유효한 채팅방 id로 참여 유저 리스트 조회 시, 참여 유저 목록 반환 성공한다.")
    void getUser_suc() throws Exception {
        //when & then
        mvc.perform(get("/chat/rooms/{roomId}/participants", anyLong()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResponseCode.CHATROOM_PARTICIPANTS_READ.getMessage()));
    }

    @UnitTest
    @WithMockUser
    @DisplayName("유효한 채팅방 id로 메세지 내역 조회 시, 메세지 내역 반환 성공한다.")
    void getMessageHistories_suc() throws Exception {
        mvc.perform(get("/chat/rooms/{roomId}/messages", anyLong()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResponseCode.CHATROOM_MESSAGE_HISTORY_READ.getMessage()));

    }

    @UnitTest
    @WithMockUser
    @DisplayName("유효한 채팅방 ID로 삭제 요청 시, 채팅방 삭제 성공한다.")
    void deleteChatRoom() throws Exception {
        //when & then
        mvc.perform(delete("/chat/rooms/{roomId}", anyLong())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResponseCode.CHATROOM_DELETE.getMessage()));

        verify(chatRoomService, times(1)).delete(anyLong());
    }

    @UnitTest
    @WithMockUser
    @DisplayName("유효한 채팅방 수정 요청 시, 채팅방 수정 성공한다.")
    void modifyChatRoom() throws Exception {
        //given
        ChatRoomModifyReqDto reqDto = ChatRoomModifyReqDto.builder().id(1L).name("Test ChatRoom Modify").capacity(8).build();

        //when & then
        mvc.perform(put("/chat/rooms/{roomId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(reqDto))
                )
                .andExpect(jsonPath("$.message").value(ResponseCode.CHATROOM_UPDATE.getMessage()));

        verify(chatRoomService, times(1)).modify(refEq(reqDto), eq(1L));
    }
}