package LinkUpTalk.chat.infrastructor;

import LinkUpTalk.chat.domain.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatroomRepository extends JpaRepository<ChatRoom, Long> {

    Page<ChatRoom> findAll(Pageable pageable);

    Page<ChatRoom> findAllByNameContaining(String word, Pageable pageable);
}
