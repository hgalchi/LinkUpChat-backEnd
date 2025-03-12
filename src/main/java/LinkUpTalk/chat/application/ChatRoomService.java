package LinkUpTalk.chat.application;

import LinkUpTalk.chat.domain.ChatMessage;
import LinkUpTalk.chat.domain.ChatRoomDetail;
import LinkUpTalk.chat.domain.constant.ChatRoomRoleType;
import LinkUpTalk.chat.domain.constant.ChatRoomType;
import LinkUpTalk.chat.domain.repository.ChatMessageRepository;
import LinkUpTalk.chat.domain.repository.ChatRoomRepository;
import LinkUpTalk.chat.presentation.dto.*;
import LinkUpTalk.user.domain.User;
import LinkUpTalk.chat.domain.ChatRoom;
import LinkUpTalk.common.response.ResponseCode;
import LinkUpTalk.common.exception.BusinessException;
import LinkUpTalk.user.domain.repository.UserRepository;
import LinkUpTalk.user.presentation.dto.UserGetResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static java.util.Arrays.stream;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public void createGroup(ChatRoomCreateReqDto dto) {
        User user = userRepository.read(dto.getUserId());
        ChatRoom chatRoom = ChatRoom.ofGroup(dto.getName(), dto.getCapacity());
        ChatRoomDetail chatRoomDetail = ChatRoomDetail.of(chatRoom, user, ChatRoomRoleType.HOST);

        chatRoom.addUser(chatRoomDetail);
        chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public void createDm(Long receiverId, String email) {
        User user = userRepository.readByEmail(email);
        User receiver = userRepository.read(receiverId);
        ChatRoom chatRoom = ChatRoom.ofDm();

        ChatRoomDetail chatRoomDetail = ChatRoomDetail.of(chatRoom, user, ChatRoomRoleType.HOST);
        ChatRoomDetail receiverChatRoomDetail = ChatRoomDetail.of(chatRoom, receiver, ChatRoomRoleType.HOST);
        chatRoom.addUser(chatRoomDetail);
        chatRoom.addUser(receiverChatRoomDetail);

        chatRoomRepository.save(chatRoom);
    }

    @Transactional(readOnly = true)
    public ChatroomGetResDto getChatRoom(Long chatRoomId) {
        ChatRoom chatroom = chatRoomRepository.read(chatRoomId);
        return chatroom.toDto();
    }

    @Transactional(readOnly = true)
    public List<ChatroomGetResDto> getChatRooms(Pageable page, String keyWord) {
        Page<ChatRoom> chatRooms = chatRoomRepository.findAllByNameContaining(keyWord,page);

        return chatRooms.stream()
                .map(ChatRoom::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResDto> getMessageHistory(Long chatRoomId){
        return chatMessageRepository.findTop100ByRoomIdOrderByCreatedAtDesc(chatRoomId).stream()
                .map(ChatMessage::toChatMessageResDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserGetResDto> getParticipants(Long roomId) {
        return chatRoomRepository.read(roomId).getParticipants().stream()
                .map(ChatRoomDetail::getUser)
                .map(User::toDto)
                .toList();
    }

    @Transactional
    public void leave(String email,Long roomId) {
        User user = userRepository.readByEmail(email);
        ChatRoom chatRoom = chatRoomRepository.read(roomId);
        ChatRoomDetail leavingUser = findLeavingUser(chatRoom, user);

        chatRoom.removeUser(leavingUser);
    }

    @Transactional
    public void delete(Long chatRoomId) {
        ChatRoom chatroom = chatRoomRepository.read(chatRoomId);
        chatroom.delete();
    }

    @Transactional
    public void modify(ChatRoomModifyReqDto dto, Long chatRoomId) {
        ChatRoom chatroom = chatRoomRepository.read(chatRoomId);
        chatroom.update(dto.getName(),dto.getCapacity());
    }

    private ChatRoomDetail findLeavingUser (ChatRoom chatRoom, User user) {
        return chatRoom.getParticipants().stream()
                .filter(userChatRoom -> userChatRoom.getUser() == user)
                .findFirst()
                .orElseThrow(() -> new BusinessException(ResponseCode.CHATROOM_USER_NOT_FOUND_IN));
    }

}
