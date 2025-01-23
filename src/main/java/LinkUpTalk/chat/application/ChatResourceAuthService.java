package LinkUpTalk.chat.application;

import LinkUpTalk.chat.domain.ChatRoomDetail;
import LinkUpTalk.chat.domain.constant.ChatRoomRoleType;
import LinkUpTalk.chat.domain.repository.ChatRoomRepository;
import LinkUpTalk.chat.domain.repository.UserChatRoomRepository;
import LinkUpTalk.user.domain.User;
import LinkUpTalk.chat.domain.ChatRoom;
import LinkUpTalk.common.response.ResponseCode;
import LinkUpTalk.common.exception.BusinessException;
import LinkUpTalk.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * 리소스의 접근권한 인증 서비스
 */
@Service
@RequiredArgsConstructor
public class ChatResourceAuthService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final UserChatRoomRepository userChatroomRepository;

    public boolean isHost(Authentication authentication, Long chatRoomId) {
        ChatRoom chatRoom = findChatRoom(chatRoomId);
        User roomHost = findChatRoomHost(chatRoom);
        User user = findUser(authentication.getName());

        return user.getId().equals(roomHost.getId());
    }

    public boolean isParticipant(Authentication authentication, Long chatRoomId) {
        ChatRoom chatroom= findChatRoom(chatRoomId);
        User user = findUser(authentication.getName());

        return user.getChatRooms().stream()
                .anyMatch(userChatRoom -> userChatRoom.getChatRoom() == chatroom);
    }

    private User findChatRoomHost(ChatRoom chatRoom) {
        return userChatroomRepository.findByChatRoom(chatRoom)
                .stream()
                .filter(userChatRoom -> userChatRoom.getRole().equals(ChatRoomRoleType.HOST))
                .map(ChatRoomDetail::getUser)
                .findFirst()
                .orElseThrow(() -> new BusinessException(ResponseCode.NOT_FOUND));
    }

    private ChatRoom findChatRoom(Long chatRoomId){
        return chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new BusinessException(ResponseCode.NOT_FOUND));
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ResponseCode.NOT_FOUND));
    }

}
