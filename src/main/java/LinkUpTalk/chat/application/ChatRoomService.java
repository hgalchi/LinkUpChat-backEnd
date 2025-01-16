package LinkUpTalk.chat.application;

import LinkUpTalk.chat.domain.UserChatroom;
import LinkUpTalk.chat.domain.constant.ChatRoomRole;
import LinkUpTalk.chat.presentation.dto.ChatroomGetResDto;
import LinkUpTalk.user.domain.User;
import LinkUpTalk.chat.domain.ChatRoom;
import LinkUpTalk.chat.presentation.dto.GroupChatRequestDto;
import LinkUpTalk.chat.infrastructor.ChatroomRepository;
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
//todo : 클래스명 수정
public class ChatRoomService {

    private final ChatroomRepository chatroomRepository;
    private final UserRepository userRepository;
    /**
     * 채팅방 생성
     */
    @Transactional
    public Long create(GroupChatRequestDto dto) {
        User user = findByUserId(dto.getUserId());
        ChatRoom chatroom = ChatRoom.builder()
                .name(dto.getName())
                .maxCount(dto.getMaxCount())
                .build();
        UserChatroom userChatroom=UserChatroom.builder()
                .chatRoom(chatroom)
                .user(user)
                .role(ChatRoomRole.HOST)
                .build();
        chatroom.addUser(userChatroom);
        chatroomRepository.save(chatroom);
        return chatroom.getId();
    }

    /**
     * 채팅방 단일 조회
     */
    public ChatroomGetResDto getChatRoom(Long roomId) {
        ChatRoom chatroom = findByChatroomId(roomId);
        return chatroom.toDto();
    }

    /**
     * 채팅방 목록 조회
     */
    //todo : Page 매핑하기
    @Transactional(readOnly = true)
    public List<ChatroomGetResDto> getChatRooms(Pageable page, String keyWord) {
        Page<ChatRoom> chatrooms = chatroomRepository.findAllByNameContaining( keyWord,page);
        return chatrooms.stream()
                .map(m -> m.toDto())
                .toList();
    }

    /**
     * 채팅방 삭제
     */
    @Transactional
    public void delete(Long roomId) {
        ChatRoom chatroom = findByChatroomId(roomId);
        chatroom.delete();
    }

    /**
     * 채팅방 수정
     */
    @Transactional
    public void modify(GroupChatRequestDto dto, Long roomId) {
        ChatRoom chatroom = findByChatroomId(roomId);
        checkMemberCount(chatroom.getCount(), dto.getMaxCount());
        chatroom.updateMaxCount(dto.getMaxCount());
    }


    private User findByUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.NOT_FOUND));
    }

    private ChatRoom findByChatroomId(Long chatroomId) {
        return chatroomRepository.findById(chatroomId)
                .orElseThrow(() -> new BusinessException(ResponseCode.NOT_FOUND));
    }

    private void checkMemberCount(int curCount, int modifyCount) {
        if (curCount > modifyCount) {
            throw new BusinessException(ResponseCode.INVALID_DESTINATION);
        }
    }
}
