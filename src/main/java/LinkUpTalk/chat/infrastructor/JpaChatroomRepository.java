package LinkUpTalk.chat.infrastructor;

import LinkUpTalk.chat.domain.ChatRoom;
import LinkUpTalk.chat.domain.repository.ChatRoomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface JpaChatroomRepository extends JpaRepository<ChatRoom, Long>, ChatRoomRepository {

}
