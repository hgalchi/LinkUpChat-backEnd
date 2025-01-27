package LinkUpTalk.chat.infrastructor;

import LinkUpTalk.chat.domain.repository.ChatRoomDetailRepository;
import LinkUpTalk.chat.domain.ChatRoomDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaChatRoomDetailRepository extends JpaRepository<ChatRoomDetail, Long>, ChatRoomDetailRepository {
}
