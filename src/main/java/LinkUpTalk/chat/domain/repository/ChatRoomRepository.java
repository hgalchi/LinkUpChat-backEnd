package LinkUpTalk.chat.domain.repository;

import LinkUpTalk.chat.domain.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ChatRoomRepository {
    Page<ChatRoom> findAllByNameContaining(String word, Pageable pageable);

    ChatRoom save(ChatRoom chatRoom);

    Optional<ChatRoom> findById(Long chatRoomId);
}
