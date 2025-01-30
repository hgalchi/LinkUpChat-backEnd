package LinkUpTalk.chat.application;

import LinkUpTalk.chat.domain.ChatRoomDetail;
import LinkUpTalk.chat.domain.constant.ChatRoomRoleType;
import LinkUpTalk.chat.domain.constant.MessageType;
import LinkUpTalk.chat.domain.repository.ChatMessageRepository;
import LinkUpTalk.chat.domain.repository.ChatRoomRepository;
import LinkUpTalk.chat.domain.repository.ChatRoomDetailRepository;
import LinkUpTalk.chat.presentation.dto.*;
import LinkUpTalk.user.domain.User;
import LinkUpTalk.chat.domain.ChatMessage;
import LinkUpTalk.chat.domain.ChatRoom;
import LinkUpTalk.common.response.ResponseCode;
import LinkUpTalk.common.exception.BusinessException;
import LinkUpTalk.user.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatRoomDetailRepository chatroomDetailRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public ChatMessageReqDto join(String email, Long roomId) {
        User user = findUserByEmail(email);
        ChatRoom chatRoom = findChatRoom(roomId);

        if(!isUserExistsInChatRoom(chatRoom, user)){
            checkRoomCapacity(chatRoom);

            ChatRoomDetail chatroomDetail = ChatRoomDetail.of(chatRoom, user, ChatRoomRoleType.MEMBER);
            chatRoom.addUser(chatroomDetail);
        }

        return ChatMessage.of(user.getName(),roomId,
                        user.getName() + " 님이 입장했습니다.",
                        MessageType.JOIN)
                .toChatMessageReqDto();

    }

    //메세지 저장
    @Transactional
    public ChatMessageReqDto saveGroupMessage(String email, Long roomId, String content) {
        User user = findUserByEmail(email);
        ChatRoom chatRoom= findChatRoom(roomId);
        ChatMessage chatMessage = ChatMessage.of(user.getName(), roomId, content, MessageType.GROUP_CHAT);

        chatMessageRepository.save(chatMessage);
        return chatMessage.toChatMessageReqDto();
    }

    // todo : fetch 전략 수정을 생각해보기
    @Transactional
    public ChatMessageReqDto saveDmMessage(String email,Long roomId, ChatMessageDmSendReqDto dto) {
        User user = findUserByEmail(email);
        ChatRoom chatRoom = findChatRoom(roomId);
        User receiver = findUser(dto.getReceiverId());
        ChatMessage chatMessage = ChatMessage.of(user.getName(), roomId, dto.getContent(), MessageType.DM_CHAT);

        chatMessageRepository.save(chatMessage);
        return chatMessage.toChatMessageReqDto(receiver.getEmail());
    }

    //todo : race condition 생각하기
    private void checkRoomCapacity(ChatRoom chatroom) {
        if (chatroom.getCapacity() <= chatroom.getParticipantCount()) {
            throw new BusinessException(ResponseCode.CHATROOM_EXCEEDED);
        }
    }

    private boolean isUserExistsInChatRoom(ChatRoom chatRoom, User user){
        return chatroomDetailRepository.findByChatRoom(chatRoom).stream()
                .anyMatch(chatRoomDetail -> chatRoomDetail.getUser() == user);
    }

    private ChatRoom findChatRoom(Long roomId){
        return chatRoomRepository.findById(roomId).orElseThrow(() -> new BusinessException(ResponseCode.CHATROOM_NOT_FOUND));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));
    }

}
