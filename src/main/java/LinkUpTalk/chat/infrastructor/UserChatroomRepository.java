package LinkUpTalk.chat.infrastructor;

import LinkUpTalk.user.domain.User;
import LinkUpTalk.chat.domain.ChatRoom;
import LinkUpTalk.chat.domain.UserChatroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserChatroomRepository extends JpaRepository<UserChatroom, Long> {

    //Optional<UserChatroom> findByUserAndChatRoom(User user, ChatRoom chatroom);

    List<UserChatroom> findByUser(User user);

    boolean existsByUserAndChatRoom(User user, ChatRoom chatRoom);

    List<UserChatroom> findAllByChatRoom(ChatRoom chatRoom);

}
