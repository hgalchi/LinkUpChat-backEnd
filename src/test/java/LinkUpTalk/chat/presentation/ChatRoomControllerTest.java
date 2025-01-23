package LinkUpTalk.chat.presentation;

import LinkUpTalk.auth.infrastructor.security.config.SecurityConfig;
import LinkUpTalk.auth.infrastructor.security.filter.JwtAuthorizationFilter;
import LinkUpTalk.chat.application.ChatRoomService;
import LinkUpTalk.chat.presentation.dto.ChatRoomCreateReqDto;
import LinkUpTalk.chat.presentation.dto.ChatRoomModifyReqDto;
import LinkUpTalk.chat.presentation.dto.ChatroomGetResDto;
import LinkUpTalk.chat.presentation.handler.WebSocketGlobalExceptionHandler;
import LinkUpTalk.common.exception.GlobalExceptionHandler;
import LinkUpTalk.common.response.ResponseCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperties;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
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
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = OncePerRequestFilter.class))
@Import({TestWebSocketConfig.class})
class ChatRoomControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ChatRoomService chatRoomService;

    @Test
    @DisplayName("채팅방 생성")
    public void createChatRoom() throws Exception {
        //given
        ChatRoomCreateReqDto reqDto = ChatRoomCreateReqDto.builder().name("Test Room").userId(1L).capacity(10).build();
        String requestBody = new ObjectMapper().writeValueAsString(reqDto);
        doNothing().when(chatRoomService).create(reqDto);

        //when & then
        mvc.perform(post("/chat/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResponseCode.CHATROOM_CREATE.getMessage()));

        verify(chatRoomService, times(1)).create(any(ChatRoomCreateReqDto.class));

    }

    @Test
    @DisplayName("채팅방 단일 조회")
    void getChatRoom_suc() throws Exception {

        //given
        Long roomId = 1L;
        ChatroomGetResDto dto = ChatroomGetResDto.builder().name("Test ChatRoom").capacity(10).build();
        when(chatRoomService.getChatRoom(1L)).thenReturn(dto);

        //when & then
        mvc.perform(get("/chat/rooms/{roomId}", roomId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResponseCode.CHATROOM_READ.getMessage()))
                .andExpect(jsonPath("$.data.name").value("Test ChatRoom"))
                .andExpect(jsonPath("$.data.capacity").value(10));

        verify(chatRoomService, times(1)).getChatRoom(roomId);
    }

    @Test
    @DisplayName("채팅방 리스트 조회")
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

        verify(chatRoomService, times(1)).getChatRooms(any(Pageable.class), anyString());
    }

    @Test
    @DisplayName("채팅방 삭제")
    void deleteChatRoom() throws Exception {
        //given
        Long roomId = 1L;
        doNothing().when(chatRoomService).delete(roomId);

        //when & then
        mvc.perform(delete("/chat/rooms/{roomId}", roomId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResponseCode.CHATROOM_DELETE.getMessage()));

        verify(chatRoomService, times(1)).delete(roomId);
    }

    @Test
    @DisplayName("채팅방 수정")
    void modifyChatRoom() throws Exception {
        //given
        Long roomId = 1L;
        ChatRoomModifyReqDto reqDto = ChatRoomModifyReqDto.builder().id(1L).name("Test ChatRoom Modify").capacity(8).build();
        String reqBody = new ObjectMapper().writeValueAsString(reqDto);
        doNothing().when(chatRoomService).modify(reqDto, roomId);

        //when & then
        mvc.perform(put("/chat/rooms/{roomId}", roomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqBody)
                )
                .andExpect(jsonPath("$.message").value(ResponseCode.CHATROOM_UPDATE.getMessage()));

        verify(chatRoomService, times(1)).modify(any(ChatRoomModifyReqDto.class), eq(1L));
    }
}