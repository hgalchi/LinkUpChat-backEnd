package LinkUpTalk.chat.application;

import LinkUpTalk.chat.domain.ChatRoom;
import LinkUpTalk.chat.domain.ChatRoomDetail;
import LinkUpTalk.chat.domain.constant.ChatRoomRoleType;
import LinkUpTalk.chat.domain.repository.ChatRoomRepository;
import LinkUpTalk.chat.presentation.dto.ChatRoomCreateReqDto;
import LinkUpTalk.chat.presentation.dto.ChatroomGetResDto;
import LinkUpTalk.chat.presentation.dto.ChatRoomModifyReqDto;
import LinkUpTalk.user.domain.User;
import LinkUpTalk.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Test
    @DisplayName("채팅방 생성할 수 있다.")
    void createChatRoom_suc() {
        //given
        ChatRoomCreateReqDto dto = ChatRoomCreateReqDto.builder()
                .name("testRoom")
                .capacity(10)
                .userId(1L)
                .build();

        User user = User.builder().id(1L).build();
        ChatRoom chatRoom = ChatRoom.builder().id(1L).name("testRoom").capacity(10).build();
        ChatRoomDetail detail = ChatRoomDetail.builder().chatRoom(chatRoom).user(user).role(ChatRoomRoleType.HOST).build();
        chatRoom.addUser(detail);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        //when
        chatRoomService.create(dto);

        //then
        verify(chatRoomRepository, times(1)).save(any(ChatRoom.class));
    }

    @Test
    @DisplayName("채팅방 식별자로 채팅방을 조회할 수 있다.")
    void getChatRoom_suc() {
        //given
        String chatRoomName = "Test chatRoom";
        ChatRoom chatRoom = ChatRoom.builder().id(1L).name(chatRoomName).capacity(10).build();

        when(chatRoomRepository.findById(1L)).thenReturn(Optional.of(chatRoom));

        //when
        ChatroomGetResDto response = chatRoomService.getChatRoom(1L);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(chatRoomName);
    }

    @Test
    @DisplayName("채팅방 수정 성공")
    void modifyChatRoom_suc(){
        //given
        ChatRoom chatRoom = ChatRoom.builder().id(1L).name("testRoom").capacity(10).build();
        String modifyChatRoomName = "Modify test chatRoom";
        int modifyCapacity = 5;
        ChatRoomModifyReqDto dto = ChatRoomModifyReqDto.builder()
                .name(modifyChatRoomName)
                .capacity(modifyCapacity)
                .build();

        when(chatRoomRepository.findById(1L)).thenReturn(Optional.of(chatRoom));

        //when
        chatRoomService.modify(dto, 1L);

        //then
        assertThat(dto.getName()).isEqualTo(modifyChatRoomName);
        assertThat(dto.getCapacity()).isEqualTo(modifyCapacity);
    }

    @Test
    @DisplayName("채팅방 삭제 성공")
    void deleteChatRoom_suc() {
        //given
        ChatRoom chatRoom = ChatRoom.builder().name("test").capacity(10).build();
        when(chatRoomRepository.findById(1L)).thenReturn(Optional.of(chatRoom));

        //when
        chatRoomService.delete(1L);

        //then
        assertThat(chatRoom.isDeleted()).isTrue();
    }

}