package LinkUpTalk.chat.application;

import LinkUpTalk.annotation.UnitTest;
import LinkUpTalk.chat.domain.ChatMessage;
import LinkUpTalk.chat.domain.ChatRoom;
import LinkUpTalk.chat.domain.ChatRoomDetail;
import LinkUpTalk.chat.domain.repository.ChatMessageRepository;
import LinkUpTalk.chat.domain.repository.ChatRoomRepository;
import LinkUpTalk.chat.presentation.dto.ChatMessageResDto;
import LinkUpTalk.chat.presentation.dto.ChatRoomCreateReqDto;
import LinkUpTalk.chat.presentation.dto.ChatroomGetResDto;
import LinkUpTalk.chat.presentation.dto.ChatRoomModifyReqDto;
import LinkUpTalk.common.exception.BusinessException;
import LinkUpTalk.user.domain.User;
import LinkUpTalk.user.domain.repository.UserRepository;
import LinkUpTalk.user.presentation.dto.UserGetResDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

    @InjectMocks
    private ChatRoomService chatRoomService;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    private User user;
    private ChatRoom chatRoom;
    private ChatRoomDetail chatRoomDetail;

    @BeforeEach
    void setUp(){
        user = User.builder().id(1L).name("user").build();
        chatRoom = ChatRoom.builder().id(1L).name("testRoom").capacity(10).build();
        chatRoomDetail = ChatRoomDetail.builder().user(user).build();
    }

    @UnitTest
    @DisplayName("사용자가 그룹 채팅방을 생성할 수 있다.")
    void createGroupChatRoom_suc() {
        //given
        ChatRoomCreateReqDto dto = ChatRoomCreateReqDto.builder()
                .name("testRoom")
                .capacity(10)
                .userId(1L)
                .build();

        when(userRepository.read(anyLong())).thenReturn(user);

        //when & then
        assertDoesNotThrow(()->chatRoomService.createGroup(dto));
        verify(chatRoomRepository, times(1)).save(any(ChatRoom.class));
    }

    @UnitTest
    @DisplayName("사용자가 다른 사용자와 1:1 채팅방을 생성할 수 있다.")
    void createDmChatRoom_suc(){
        //given
        Long receiverId = 2L;
        String email = "user@naver.com";

        User sender = User.builder().id(1L).build();
        User receiver = User.builder().id(receiverId).build();

        when(userRepository.readByEmail(email)).thenReturn(sender);
        when(userRepository.read(receiverId)).thenReturn(receiver);

        //when &then
        assertDoesNotThrow(() -> chatRoomService.createDm(receiverId, email));
        verify(chatRoomRepository, times(1)).save(any(ChatRoom.class));
    }
    @UnitTest
    @DisplayName("사용자가 채팅방 식별자로 채팅방 정보를 조회할 수 있다.")
    void getChatRoom_suc() {
        //given
        when(chatRoomRepository.read(anyLong())).thenReturn(chatRoom);

        //when
        ChatroomGetResDto response = chatRoomService.getChatRoom(1L);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(chatRoom.getName());
    }

    @UnitTest
    @DisplayName("사용자는 키워드로 채팅방 목록을 검색할 수 있다.")
    void getChatRooms_suc(){
        //given
        Page<ChatRoom> mockPage = new PageImpl<>(List.of(
                ChatRoom.builder().name("test1").build(),
                ChatRoom.builder().name("test2").build()
        ));

        when(chatRoomRepository.findAllByNameContaining(anyString(), nullable(Pageable.class))).thenReturn(mockPage);

        //when
        List<ChatroomGetResDto> result = chatRoomService.getChatRooms(any(Pageable.class), anyString());

        //then
        assertThat(result).hasSize(2)
                .extracting("name")
                .containsExactly("test1", "test2");

    }

    @UnitTest
    @DisplayName("사용자는 채팅방 식별자로 채팅 메시지 내역을 조회할 수 있다.")
    void getMessageHistory_suc(){
        //given
        List<ChatMessage> mockMessage = List.of(
                ChatMessage.builder().content("message1").build(),
                ChatMessage.builder().content("message2").build()
        );

        when(chatMessageRepository.findTop100ByRoomIdOrderByCreatedAtDesc(anyLong())).thenReturn(mockMessage);

        //when
        List<ChatMessageResDto> result = chatRoomService.getMessageHistory(anyLong());

        //then 값 확인하기
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getContent()).isEqualTo("message1");
        assertThat(result.get(1).getContent()).isEqualTo("message2");

    }

    @UnitTest
    @DisplayName("사용자는 채팅방 식별자로 참여자 목록을 조회할 수 있다.")
    void getParticipants_suc(){
        //given
        User user1 = User.builder().name("user1").build();
        User user2 = User.builder().name("user2").build();
        ChatRoomDetail chatRoomDetail1=ChatRoomDetail.builder().user(user1).build();
        ChatRoomDetail chatRoomDetail2=ChatRoomDetail.builder().user(user2).build();

        ChatRoom chatRoom = mock(ChatRoom.class);

        when(chatRoomRepository.read(anyLong())).thenReturn(chatRoom);
        when(chatRoom.getParticipants()).thenReturn(new LinkedHashSet<>(List.of(chatRoomDetail1,chatRoomDetail2)));

        //when
        List<UserGetResDto> participants = chatRoomService.getParticipants(anyLong());

        //then
        assertThat(participants)
                .hasSize(2)
                .extracting("name")
                .containsExactly("user1","user2");
    }
    @UnitTest
    @DisplayName("사용자는 채팅방에 나갈 수 있다.")
    void leave_suc(){
        //given
        chatRoom.addUser(chatRoomDetail);

        when(userRepository.readByEmail(anyString())).thenReturn(user);
        when(chatRoomRepository.read(anyLong())).thenReturn(chatRoom);

        //when
        assertDoesNotThrow(() -> chatRoomService.leave("user@naver.com", 1L));
    }

    @UnitTest
    @DisplayName("채팅방을 나가려하지만 채팅방에 존재하지 않아 요청을 실패한다.")
    void leave_failWithNotInChatRoom(){
        when(userRepository.readByEmail(anyString())).thenReturn(user);
        when(chatRoomRepository.read(anyLong())).thenReturn(chatRoom);

        //when & then
        assertThrows(BusinessException.class, () -> chatRoomService.leave("room123", 123L));
    }



    @UnitTest
    @DisplayName("사용자는 채팅방 정보를 수정할 수 있다.")
    void modifyChatRoom_suc() {
        //given
        ChatRoomModifyReqDto dto = ChatRoomModifyReqDto.builder()
                .name("Modify test chatRoom")
                .capacity(5)
                .build();

        when(chatRoomRepository.read(anyLong())).thenReturn(chatRoom);

        //when
        chatRoomService.modify(dto, 1L);

        //then
        assertThat(dto.getName()).isEqualTo("Modify test chatRoom");
        assertThat(dto.getCapacity()).isEqualTo(5);
    }

    @UnitTest
    @DisplayName("사용자는 채팅방을 삭제할 수 있다.")
    void deleteChatRoom_suc() {
        //given
        when(chatRoomRepository.read(anyLong())).thenReturn(chatRoom);

        //when
        chatRoomService.delete(1L);

        //then
        assertThat(chatRoom.isDeleted()).isTrue();
    }

}