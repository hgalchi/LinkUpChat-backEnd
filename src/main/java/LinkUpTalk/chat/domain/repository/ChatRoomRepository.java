package LinkUpTalk.chat.domain.repository;

import LinkUpTalk.chat.domain.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ChatRoomRepository {
    //todo : JPQL, QueryDsl등으로 직접 작성한 쿼리문의 경우 필수적으로 테스트를 작성한다.
    Page<ChatRoom> findAllByNameContaining(String word, Pageable pageable);

    ChatRoom save(ChatRoom chatRoom);

    Optional<ChatRoom> findById(Long chatRoomId);
}
