package LinkUpTalk.chat.infrastructor;


import LinkUpTalk.chat.domain.ChatMessage;
import LinkUpTalk.chat.domain.repository.ChatMessageRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

public interface JpaChatMessageRepository extends MongoRepository<ChatMessage, String>, ChatMessageRepository {
}
