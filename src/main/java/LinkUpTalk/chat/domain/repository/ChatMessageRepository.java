package LinkUpTalk.chat.domain.repository;

import LinkUpTalk.chat.domain.ChatMessage;

import java.util.Optional;

public interface ChatMessageRepository {
    ChatMessage save(ChatMessage chatMessage);
}
