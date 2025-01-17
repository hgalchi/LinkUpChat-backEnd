package LinkUpTalk.chat.presentation;

import LinkUpTalk.chat.domain.ChatRoom;
import LinkUpTalk.chat.infrastructor.ChatroomRepository;
import LinkUpTalk.chat.presentation.dto.GroupChatRequestDto;
import LinkUpTalk.user.domain.User;
import LinkUpTalk.util.TestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class ChatControllerTest {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext ctx;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ChatroomRepository chatroomRepository;

    @Autowired
    private TestUtil testUtil;

    private User user;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(ctx)
                // .apply(springSecurity())
                .alwaysDo(print())
                .build();

        user = testUtil.registerUser();
    }

    @Test
    @DisplayName("채팅방 생성")
    public void createChatRoom() throws Exception {
        //given
        Long userId = user.getId();
        String chatRoomName = "spring chat Room!!";
        int maxCount = 5;
        GroupChatRequestDto createGroupChatRoomDto = GroupChatRequestDto.builder()
                .userId(userId)
                .name(chatRoomName)
                .maxCount(maxCount)
                .build();

        //when
        MvcResult result = mvc.perform(post("/chat/rooms")
                        .content(mapper.writeValueAsString(createGroupChatRoomDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        String responseBody = result.getResponse().getContentAsString();
        Map<String, Long> responseDto = mapper.readValue(responseBody, Map.class);
        Long chatRoomId = responseDto.get("chatRoomId");

        assertNotNull(responseDto);
        assertNotNull(chatRoomId);

        ChatRoom chatRoom = chatroomRepository.findById(chatRoomId).orElse(null);
        assertNotNull(chatRoom);
        assertEquals(chatRoomName, chatRoom.getName());
    }

    @Test
    @DisplayName("채팅방 수정")
    public void modifyChatroom(){
        Long userId = user.getId();
    }

}