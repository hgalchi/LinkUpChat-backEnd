package LinkUpTalk.chat.domain.repository;

import LinkUpTalk.chat.domain.ChatMessage;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository {
    ChatMessage save(ChatMessage chatMessage);
    List<ChatMessage> findChatMessageByRoomId(Long roomId);
}
