package LinkUpTalk.chat.domain.repository;

import LinkUpTalk.chat.domain.ChatRoom;
import LinkUpTalk.chat.domain.ChatRoomDetail;
import LinkUpTalk.user.domain.User;

import java.util.List;

public interface ChatRoomDetailRepository {

    List<ChatRoomDetail> findByChatRoom(ChatRoom chatroom);

    List<ChatRoomDetail> findByUser(User user);

    boolean existsByUserAndChatRoom(User user, ChatRoom chatRoom);
}
