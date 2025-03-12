package LinkUpTalk.chat.application;

import LinkUpTalk.chat.domain.ChatRoomDetail;
import LinkUpTalk.chat.domain.constant.ChatRoomRoleType;
import LinkUpTalk.chat.domain.repository.ChatRoomRepository;
import LinkUpTalk.chat.domain.repository.ChatRoomDetailRepository;
import LinkUpTalk.user.domain.User;
import LinkUpTalk.chat.domain.ChatRoom;
import LinkUpTalk.common.response.ResponseCode;
import LinkUpTalk.common.exception.BusinessException;
import LinkUpTalk.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatResourceAuthService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatRoomDetailRepository chatroomDetailRepository;

    public boolean isHost(Authentication authentication, Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.read(chatRoomId);
        User roomHost = getRoomHost(chatRoom);
        User user = userRepository.readByEmail(authentication.getName());

        return user.getId().equals(roomHost.getId());
    }

    public boolean isParticipant(Authentication authentication, Long chatRoomId) {
        ChatRoom chatroom= chatRoomRepository.read(chatRoomId);
        User user = userRepository.readByEmail(authentication.getName());

        return chatroom.getParticipants().stream()
                .anyMatch(userDetail -> userDetail.getUser() == user);
    }

    private User getRoomHost(ChatRoom chatRoom) {
        return chatroomDetailRepository.findByChatRoom(chatRoom)
                .stream()
                .filter(userChatRoom -> userChatRoom.getRole().equals(ChatRoomRoleType.HOST))
                .map(ChatRoomDetail::getUser)
                .findFirst()
                .orElseThrow(() -> new BusinessException(ResponseCode.NOT_FOUND));
    }
}
