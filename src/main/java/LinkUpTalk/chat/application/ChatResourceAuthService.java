package LinkUpTalk.chat.application;

import LinkUpTalk.chat.domain.constant.ChatRoomRole;
import LinkUpTalk.user.domain.User;
import LinkUpTalk.chat.domain.ChatRoom;
import LinkUpTalk.chat.infrastructor.ChatroomRepository;
import LinkUpTalk.chat.infrastructor.UserChatroomRepository;
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

    private final ChatroomRepository chatroomRepository;
    private final UserRepository userRepository;
    private final UserChatroomRepository userChatroomRepository;

    /**
     * 사용자가 리소스의 주인임을 체크
     */
    public boolean isRoomOwner(Authentication authentication, Long roomId) {
        ChatRoom chatroom = chatroomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ResponseCode.NOT_FOUND));

        User roomHost = userChatroomRepository.findAllByChatRoom(chatroom)
                .stream()
                .filter(f -> f.getRole().equals(ChatRoomRole.HOST))
                .map(m -> m.getUser())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ResponseCode.NOT_FOUND));

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BusinessException(ResponseCode.NOT_FOUND));

        return user.getId().equals(roomHost.getId());
    }

    public boolean isRoomMemeber(Authentication authentication, Long roomId) {
        ChatRoom chatroom=chatroomRepository.findById(roomId).get();
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BusinessException(ResponseCode.NOT_FOUND));
        return userChatroomRepository.existsByUserAndChatRoom(user, chatroom);
    }



}
