package LinkUpTalk.chat.application;

import LinkUpTalk.chat.domain.ChatRoomDetail;
import LinkUpTalk.chat.domain.constant.ChatRoomRoleType;
import LinkUpTalk.chat.domain.repository.ChatRoomRepository;
import LinkUpTalk.chat.presentation.dto.ChatRoomCreateReqDto;
import LinkUpTalk.chat.presentation.dto.ChatroomGetResDto;
import LinkUpTalk.user.domain.User;
import LinkUpTalk.chat.domain.ChatRoom;
import LinkUpTalk.chat.presentation.dto.ChatRoomModifyReqDto;
import LinkUpTalk.common.response.ResponseCode;
import LinkUpTalk.common.exception.BusinessException;
import LinkUpTalk.user.domain.repository.UserRepository;
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

    @Transactional
    public void create(ChatRoomCreateReqDto dto) {
        User user = findUser(dto.getUserId());
        ChatRoom chatRoom = ChatRoom.of(dto.getName(), dto.getCapacity());
        ChatRoomDetail chatRoomDetail = ChatRoomDetail.of(chatRoom, user, ChatRoomRoleType.HOST);

        chatRoom.addUser(chatRoomDetail);
        chatRoomRepository.save(chatRoom);
    }

    public ChatroomGetResDto getChatRoom(Long chatRoomId) {
        ChatRoom chatroom = findChatRoom(chatRoomId);
        return chatroom.toDto();
    }

    @Transactional(readOnly = true)
    public List<ChatroomGetResDto> getChatRooms(Pageable page, String keyWord) {
        Page<ChatRoom> chatRooms = chatRoomRepository.findAllByNameContaining(keyWord,page);
        return chatRooms.stream()
                .map(ChatRoom::toDto)
                .toList();
    }

    @Transactional
    public void getMessageHistory(Long chatRoomId){
        //chatMessage를 전송

    }

    @Transactional
    public void delete(Long chatRoomId) {
        ChatRoom chatroom = findChatRoom(chatRoomId);
        chatroom.delete();
    }

    @Transactional
    public void modify(ChatRoomModifyReqDto dto, Long chatRoomId) {
        ChatRoom chatroom = findChatRoom(chatRoomId);
        chatroom.update(dto.getName(),dto.getCapacity());
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.NOT_FOUND));
    }

    private ChatRoom findChatRoom(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new BusinessException(ResponseCode.NOT_FOUND));
    }

}
