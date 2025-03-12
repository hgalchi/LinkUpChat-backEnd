package LinkUpTalk.chat.infrastructor.jpa;

import LinkUpTalk.chat.domain.ChatRoom;
import LinkUpTalk.chat.domain.repository.ChatRoomRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaChatroomRepository extends ExtendedRepository<ChatRoom,Long>, ChatRoomRepository {

}
