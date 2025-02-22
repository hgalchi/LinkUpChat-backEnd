package LinkUpTalk.chat.application;

import LinkUpTalk.annotation.UnitTest;
import LinkUpTalk.chat.domain.ChatMessage;
import LinkUpTalk.chat.domain.ChatRoom;
import LinkUpTalk.chat.domain.ChatRoomDetail;
import LinkUpTalk.chat.domain.constant.MessageType;
import LinkUpTalk.chat.domain.repository.ChatMessageRepository;
import LinkUpTalk.chat.domain.repository.ChatRoomDetailRepository;
import LinkUpTalk.chat.domain.repository.ChatRoomRepository;
import LinkUpTalk.chat.presentation.dto.ChatMessageDmSendReqDto;
import LinkUpTalk.chat.presentation.dto.ChatMessageReqDto;
import LinkUpTalk.common.exception.BusinessException;
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


    @UnitTest
    @DisplayName("사용자는 채팅방에 입장할 수 있다.")
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

        when(chatRoomRepository.read(ROOM_ID)).thenReturn(chatRoom);
        when(userRepository.readByEmail(PRODUCER_EMAIL)).thenReturn(user);
        when(chatRoomDetailRepository.findByChatRoom(chatRoom)).thenReturn(List.of(chatRoomDetail));

        //when
        ChatMessageReqDto dto = chatService.join(PRODUCER_EMAIL, ROOM_ID);

        assertThat(dto).usingRecursiveComparison().isEqualTo(expected);
    }

    @UnitTest
    @DisplayName("사용자 입장 시 방정원이 초과하면 요청은 실패한다.")
    void join_failWithCapacityOver(){
        //given
        ChatRoom chatRoom = ChatRoom.builder().name("Test chatRoom").capacity(8).participantCount(8).build();
        User user = User.builder().name(PRODUCER).build();

        when(chatRoomRepository.read(ROOM_ID)).thenReturn(chatRoom);
        when(userRepository.readByEmail(PRODUCER_EMAIL)).thenReturn(user);

        //when & then
        assertThrows(BusinessException.class, () -> chatService.join(PRODUCER_EMAIL, ROOM_ID));
    }


    @UnitTest
    @DisplayName("그룹 채팅 메시지를 저장할 수 있다.")
    void saveMessage_suc() {
        //given
        ChatMessageReqDto expected = ChatMessageReqDto.builder()
                .sender(PRODUCER)
                .destination(ROOM_ID)
                .content(GROUP_MESSAGE)
                .messageType(MessageType.GROUP_CHAT).build();


        ChatRoom chatRoom = ChatRoom.builder().name("Test chatRoom").capacity(8).build();
        User user = User.builder().name("Producer").build();

        when(chatRoomRepository.read(ROOM_ID)).thenReturn(chatRoom);
        when(userRepository.readByEmail(PRODUCER_EMAIL)).thenReturn(user);

        ChatMessageReqDto dto = chatService.saveGroupMessage(PRODUCER_EMAIL, ROOM_ID, GROUP_MESSAGE);

        assertThat(dto).usingRecursiveComparison().isEqualTo(expected);
        verify(chatMessageRepository, times(1)).save(any(ChatMessage.class));
    }

    @UnitTest
    @DisplayName("개인 채팅 메시지를 저장할 수 있다.")
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

        when(chatRoomRepository.read(ROOM_ID)).thenReturn(chatRoom);
        when(userRepository.readByEmail(PRODUCER_EMAIL)).thenReturn(producer);
        when(userRepository.read(2L)).thenReturn(receiver);

        ChatMessageReqDto dto = chatService.saveDmMessage(PRODUCER_EMAIL,ROOM_ID, reqDto);

        assertThat(dto).usingRecursiveComparison().isEqualTo(expected);
        verify(chatMessageRepository, times(1)).save(any(ChatMessage.class));
    }


}