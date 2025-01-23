package LinkUpTalk.chat.infrastructor;

import LinkUpTalk.chat.domain.repository.UserChatRoomRepository;
import LinkUpTalk.chat.domain.ChatRoomDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserChatRoomRepository extends JpaRepository<ChatRoomDetail, Long>, UserChatRoomRepository {
}
