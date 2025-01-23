package LinkUpTalk.chat.application;

import LinkUpTalk.chat.domain.ChatRoomDetail;
import LinkUpTalk.chat.domain.constant.ChatRoomRoleType;
import LinkUpTalk.chat.domain.repository.ChatMessageRepository;
import LinkUpTalk.chat.domain.repository.ChatRoomRepository;
import LinkUpTalk.chat.domain.repository.UserChatRoomRepository;
import LinkUpTalk.user.domain.User;
import LinkUpTalk.chat.domain.ChatMessage;
import LinkUpTalk.chat.domain.ChatRoom;
import LinkUpTalk.chat.presentation.dto.ChatMessageDto;
import LinkUpTalk.common.response.ResponseCode;
import LinkUpTalk.common.exception.BusinessException;
import LinkUpTalk.user.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class ChatService {


    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final UserChatRoomRepository userChatroomRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public void join(String email, Long roomId) {
        ChatRoom chatRoom = findChatRoom(roomId);
        User user = findUser(email);
        checkIfUserExists(chatRoom, user);
        checkRoomCapacity(chatRoom);

        ChatRoomDetail chatroomDetail = ChatRoomDetail.of(chatRoom, user, ChatRoomRoleType.MEMBER);
        chatRoom.addUser(chatroomDetail);
    }

    //사용자가 방에서 나감.
    @Transactional
    public void leave(String email,Long roomId) {
        User user = findUser(email);
        ChatRoom chatRoom = findChatRoom(roomId);
        ChatRoomDetail leavingUser = findLeavingUser(chatRoom, user);

        chatRoom.removeUser(leavingUser);
    }

    //메세지 전송
    @Transactional
    public void saveMessage(ChatMessageDto message, Long roomId, String email) {
        ChatRoom chatRoom = findChatRoom(roomId);
        ChatMessage chatmessage = ChatMessage.builder()
                .message(message.getMessage())
                .chatRoom(chatRoom)
                .sender(email)
                .build();

        chatMessageRepository.save(chatmessage);
    }

    //todo : race condition 생각하기
    private void checkRoomCapacity(ChatRoom chatroom) {
        if (chatroom.getCapacity() <= chatroom.getParticipantCount()) {
            throw new MessageDeliveryException("현재 채팅방 인원이 초과되었습니다.");
        }
    }
    private void checkIfUserExists(ChatRoom chatRoom,User user){
        userChatroomRepository.findByChatRoom(chatRoom).stream()
                .filter(userChatRoom -> userChatRoom.getUser() == user)
                .findAny()
                .ifPresent(userChatroom -> {
                    //이미 채팅룸에 존재하는 회원임
                    return;
                });
    }

    private ChatRoom findChatRoom(Long roomId){
        return chatRoomRepository.findById(roomId).orElseThrow(() -> new UsernameNotFoundException("채팅방을 찾을 수 없습니다."));
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));
    }

    private ChatRoomDetail findLeavingUser (ChatRoom chatRoom, User user) {
        return chatRoom.getParticipants().stream()
                .filter(userChatRoom -> userChatRoom.getUser() == user)
                .findFirst()
                .orElseThrow(() -> new BusinessException(ResponseCode.CHATROOM_USER_NOT_FOUND_IN));
    }
}
