package LinkUpTalk.chat.application;

import LinkUpTalk.annotation.UnitTest;
import LinkUpTalk.chat.domain.ChatRoom;
import LinkUpTalk.chat.domain.ChatRoomDetail;
import LinkUpTalk.chat.domain.constant.ChatRoomRoleType;
import LinkUpTalk.chat.domain.repository.ChatRoomDetailRepository;
import LinkUpTalk.chat.domain.repository.ChatRoomRepository;
import LinkUpTalk.common.exception.BusinessException;
import LinkUpTalk.common.response.ResponseCode;
import LinkUpTalk.user.domain.User;
import LinkUpTalk.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.data.util.Predicates.isFalse;

@ExtendWith(MockitoExtension.class)
class ChatResourceAuthServiceTest {

    @InjectMocks
    private ChatResourceAuthService chatResourceAuthService;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChatRoomDetailRepository chatroomDetailRepository;

    @Mock
    private Authentication authentication;

    @UnitTest
    @DisplayName("사용자가 채팅방 호스트 권한을 갖는지 확인할 수 있다.")
    void isHost_Suc() {
        // Given
        Long chatRoomId = 1L;
        String userEmail = "test@example.com";
        User user = User.builder().id(1L).email(userEmail).build();
        User hostUser = User.builder().id(1L).build();
        ChatRoom chatRoom = ChatRoom.builder().id(chatRoomId).build();
        ChatRoomDetail chatRoomDetail = ChatRoomDetail.builder().chatRoom(chatRoom).user(hostUser).role(ChatRoomRoleType.HOST).build();

        when(authentication.getName()).thenReturn(userEmail);
        when(userRepository.readByEmail(userEmail)).thenReturn(user);
        when(chatRoomRepository.read(chatRoomId)).thenReturn(chatRoom);
        when(chatroomDetailRepository.findByChatRoom(chatRoom)).thenReturn(List.of(chatRoomDetail));

        // When
        boolean isHost = chatResourceAuthService.isHost(authentication, chatRoomId);

        // Then
        assertThat(isHost).isTrue();
    }

    @UnitTest
    @DisplayName("사용자가 호스트가 아닌 경우 요청은 실패한다.")
    void isHost_failWithNotHost() {
        Long chatRoomId = 1L;
        String userEmail = "test@example.com";

        User MemberUser = User.builder().id(1L).build();
        ChatRoom chatRoom = ChatRoom.builder().id(chatRoomId).build();
        ChatRoomDetail chatRoomDetail = ChatRoomDetail.builder().chatRoom(chatRoom).user(MemberUser).role(ChatRoomRoleType.MEMBER).build();

        when(chatRoomRepository.read(chatRoomId)).thenReturn(chatRoom);
        when(chatroomDetailRepository.findByChatRoom(chatRoom)).thenReturn(List.of(chatRoomDetail));

        // When
        assertThrows(BusinessException.class, () -> chatResourceAuthService.isHost(authentication, chatRoomId));

    }

    @UnitTest
    @DisplayName("사용자가 채팅방 참가자 권한이 있는지 확인할 수 있다.")
    void isParticipant_Suc() {

        // Given
        Long chatRoomId = 1L;
        String userEmail = "test@example.com";
        User user = User.builder().id(1L).email(userEmail).build();
        ChatRoom chatRoom = ChatRoom.builder().id(chatRoomId).build();
        ChatRoomDetail chatRoomDetail = ChatRoomDetail.builder().chatRoom(chatRoom).user(user).role(ChatRoomRoleType.MEMBER).build();
        chatRoom.addUser(chatRoomDetail);

        when(authentication.getName()).thenReturn(userEmail);
        when(userRepository.readByEmail(userEmail)).thenReturn(user);
        when(chatRoomRepository.read(chatRoomId)).thenReturn(chatRoom);

        // When
        boolean isParticipant = chatResourceAuthService.isParticipant(authentication, chatRoomId);

        // Then
        assertThat(isParticipant).isTrue();
    }

    @UnitTest
    @DisplayName("사용자가 채팅방 참가자 권한이 없는 경우 요청은 실패한다.")
    void isParticipant_failWithNotParticipant(){
        // Given
        Long chatRoomId = 1L;
        String userEmail = "test@example.com";

        User Member = User.builder().id(1L).email(userEmail).build();
        User user = User.builder().id(2L).email("dkfdkf@naver.com").build();

        ChatRoom chatRoom = ChatRoom.builder().id(chatRoomId).build();
        ChatRoomDetail chatRoomDetail = ChatRoomDetail.builder().chatRoom(chatRoom).user(Member).role(ChatRoomRoleType.MEMBER).build();
        chatRoom.addUser(chatRoomDetail);

        when(authentication.getName()).thenReturn(userEmail);
        when(userRepository.readByEmail(userEmail)).thenReturn(user);
        when(chatRoomRepository.read(chatRoomId)).thenReturn(chatRoom);

        // When
        boolean isParticipant = chatResourceAuthService.isParticipant(authentication, chatRoomId);

        // Then
        assertThat(isParticipant).isFalse();
    }
}