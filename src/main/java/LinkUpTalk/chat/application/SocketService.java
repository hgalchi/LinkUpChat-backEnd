package LinkUpTalk.chat.application;

import LinkUpTalk.chat.domain.constant.ChatRoomRole;
import LinkUpTalk.user.domain.User;
import LinkUpTalk.chat.domain.ChatMessage;
import LinkUpTalk.chat.domain.ChatRoom;
import LinkUpTalk.chat.domain.UserChatroom;
import LinkUpTalk.chat.infrastructor.ChatmessageRepository;
import LinkUpTalk.chat.infrastructor.ChatroomRepository;
import LinkUpTalk.chat.infrastructor.UserChatroomRepository;
import LinkUpTalk.chat.dto.ChatMessageDto;
import LinkUpTalk.common.response.ResponseCode;
import LinkUpTalk.common.exception.BusinessException;
import LinkUpTalk.user.infrastructor.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Log4j2
public class SocketService {


    private final ChatroomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final UserChatroomRepository userChatroomRepository;
    private final ChatmessageRepository chatMessageRepository;

    /**
     * 채팅방 입장Us
     * @param email 유저 email
     * @param roomId 입장하는 room pk
     */
    //todo : 채팅방 비밀번호 설정
    @Transactional
    public void join(String email,Long roomId) {
        ChatRoom chatRoom = findByRoomId(roomId);
        checkMemberCount(chatRoom);
        User user = findByEmail(email);

        UserChatroom userChatroom = UserChatroom.of(chatRoom, user, ChatRoomRole.MEMBER);
        chatRoom.addUser(userChatroom);
        chatRoom.setUserCount(chatRoom.getCount()+1);
    }



    /**
     * 채팅방 나가기
     * @param email 유저 email
     * @param roomId 퇴장하는 room pk
     */
    @Transactional
    public void leave(String email,Long roomId) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));
        ChatRoom chatroom = chatRoomRepository.findById(roomId).orElseThrow(() -> new UsernameNotFoundException("채팅방을 찾을 수 없습니다."));
        //UserChatroom userChatroom = userChatroomRepository.findByUserAndChatRoom(user, chatroom).orElseThrow(() -> new UsernameNotFoundException("채팅방에 해당 회원이 존재하지 않습니다."));
        //chatroom.removeDetail(userChatroom);
        chatroom.setUserCount(chatroom.getCount()-1);
        chatRoomRepository.save(chatroom);
    }

    /**
     * 채팅 메세지 저장
     * @param message 유저가 보낸 메세지
     * @param roomId room pk
     * @param email 유저 email
     */
    public void saveMessage(ChatMessageDto message, Long roomId, String email) {
        ChatRoom chatroom = chatRoomRepository.findById(roomId).orElseThrow(() -> new BusinessException(ResponseCode.NOT_FOUND));
        ChatMessage chatmessage = ChatMessage.builder()
                .message(message.getMessage())
                .chatRoom(chatroom)
                .sender(email)
                .time(LocalDateTime.now())
                .build();
        chatMessageRepository.save(chatmessage);
    }

    public void saveMessageWithImage(ChatMessageDto dto,Long roomId, String email) {
        ChatRoom chatroom = chatRoomRepository.findById(roomId).orElseThrow(() -> new BusinessException(ResponseCode.NOT_FOUND));
    }

    public void enterRoom(String email, Long roomId) {
        //새로입장한 유저일경우
        // 재접속한 유저일경우
        // 잘못된 요청일 경우 ( 채팅룸에 존재하지 않은 유저가 데이터를 요청할 경우 )

    }

    private void checkMemberCount(ChatRoom chatroom) {
        if (chatroom.getMaxCount() <= chatroom.getCount()) {
            throw new MessageDeliveryException("현재 채팅방 인원이 초과되었습니다.");
        }
    }

    private ChatRoom findByRoomId(Long roomId){
        return chatRoomRepository.findById(roomId).orElseThrow(() -> new UsernameNotFoundException("채팅방을 찾을 수 없습니다."));
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));
    }

    private String BinaryImageChange(String imageDate) {

        //","을 기준으로 바이트 코드를 나눈다.
        String[] Strings = imageDate.split(",");
        String base64 = Strings[1];
        String extension = "";

        //if문을 통해 확장자명을 지정
        switch (Strings[0]) {
            case "data:image/jpeg;base64":
                extension = "jpeg";
            case "data:image/png;base64":
                extension = "png";
            default:
                extension = "jpg";
        }
        //파일을 db에 저장하고 찾는 방법
        log.info("이미지 바이너리 데이터 : " + base64);

        //db에 폴더 위치를 저장
        return " ";

    }
}
