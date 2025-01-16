package LinkUpTalk.chat.infrastructor;


import LinkUpTalk.chat.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatmessageRepository extends JpaRepository<ChatMessage, Long> {

}
