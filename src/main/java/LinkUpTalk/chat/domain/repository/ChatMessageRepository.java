package LinkUpTalk.chat.domain.repository;

import LinkUpTalk.chat.domain.ChatMessage;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository {
    ChatMessage save(ChatMessage chatMessage);

    @Query(value = "{ 'roomId':?0}", sort = "{createdAt: -1}")
    List<ChatMessage> findTop100ByRoomIdOrderByCreatedAtDesc(Long roomId);
}
