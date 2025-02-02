package LinkUpTalk.chat.application;

import LinkUpTalk.chat.domain.ChatMessage;
import LinkUpTalk.chat.domain.ChatRoom;
import LinkUpTalk.chat.domain.ChatRoomDetail;
import LinkUpTalk.chat.domain.constant.MessageType;
import LinkUpTalk.chat.domain.repository.ChatMessageRepository;
import LinkUpTalk.chat.domain.repository.ChatRoomDetailRepository;
import LinkUpTalk.chat.domain.repository.ChatRoomRepository;
import LinkUpTalk.chat.presentation.dto.ChatMessageDmSendReqDto;
import LinkUpTalk.chat.presentation.dto.ChatMessageReqDto;
import LinkUpTalk.user.domain.User;
import LinkUpTalk.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @InjectMocks
    private ChatService chatService;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChatRoomDetailRepository chatRoomDetailRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    private static final String PRODUCER = "Producer";
    private static final String RECEIVER = "Receiver";
    private static final String PRODUCER_EMAIL = "producer@naver.com";
    private static final String RECEIVER_EMAIL = "receiver@naver.com";
    private static final Long ROOM_ID = 1L;
    private static final String GROUP_MESSAGE = "다들 안녕하세요";
    private static final String DM_MESSAGE = "처음 봬요";


    @Test
    @DisplayName("채팅방 입장")
    void join_suc(){
        //given
        ChatMessageReqDto expected = ChatMessageReqDto.builder()
                .sender(PRODUCER)
                .destination(ROOM_ID)
                .content("Producer 님이 입장했습니다.")
                .messageType(MessageType.JOIN)
                .build();

        ChatRoom chatRoom = ChatRoom.builder().name("Test chatRoom").capacity(8).build();
        User user = User.builder().name(PRODUCER).build();
        ChatRoomDetail chatRoomDetail = ChatRoomDetail.builder().build();

        when(chatRoomRepository.findById(ROOM_ID)).thenReturn(Optional.of(chatRoom));
        when(userRepository.findByEmail(PRODUCER_EMAIL)).thenReturn(Optional.of(user));
        when(chatRoomDetailRepository.findByChatRoom(chatRoom)).thenReturn(List.of(chatRoomDetail));

        //when
        ChatMessageReqDto dto = chatService.join(PRODUCER_EMAIL, ROOM_ID);

        assertThat(dto).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("그룹 채팅 메시지 저장")
    void saveMessage_suc() {
        //given
        ChatMessageReqDto expected = ChatMessageReqDto.builder()
                .sender(PRODUCER)
                .destination(ROOM_ID)
                .content(GROUP_MESSAGE)
                .messageType(MessageType.GROUP_CHAT).build();


        ChatRoom chatRoom = ChatRoom.builder().name("Test chatRoom").capacity(8).build();
        User user = User.builder().name("Producer").build();

        when(chatRoomRepository.findById(ROOM_ID)).thenReturn(Optional.of(chatRoom));
        when(userRepository.findByEmail(PRODUCER_EMAIL)).thenReturn(Optional.of(user));

        ChatMessageReqDto dto = chatService.saveGroupMessage(PRODUCER_EMAIL, ROOM_ID, GROUP_MESSAGE);

        assertThat(dto).usingRecursiveComparison().isEqualTo(expected);
        verify(chatMessageRepository, times(1)).save(any(ChatMessage.class));
    }

    @Test
    @DisplayName("개인 채팅 메시지 저장")
    void saveDmMessage() {
        //given
        ChatMessageDmSendReqDto reqDto = ChatMessageDmSendReqDto.builder().content(DM_MESSAGE).receiverId(2L).build();

        ChatMessageReqDto expected = ChatMessageReqDto.builder()
                .sender(PRODUCER)
                .destination(RECEIVER_EMAIL)
                .content(DM_MESSAGE)
                .messageType(MessageType.DM_CHAT)
                .build();

        ChatRoom chatRoom = ChatRoom.builder().name("Test chatRoom").capacity(8).build();
        User producer = User.builder().name(PRODUCER).build();
        User receiver = User.builder().name(RECEIVER).email(RECEIVER_EMAIL).build();

        when(chatRoomRepository.findById(ROOM_ID)).thenReturn(Optional.of(chatRoom));
        when(userRepository.findByEmail(PRODUCER_EMAIL)).thenReturn(Optional.of(producer));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));

        ChatMessageReqDto dto = chatService.saveDmMessage(PRODUCER_EMAIL,ROOM_ID, reqDto);

        assertThat(dto).usingRecursiveComparison().isEqualTo(expected);
        verify(chatMessageRepository, times(1)).save(any(ChatMessage.class));
    }


}