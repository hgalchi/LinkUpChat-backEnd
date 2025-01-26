package LinkUpTalk.chat.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection="chatting_content")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class ChatMessage {

    @Id
    private String id;

    private String sender;

    private Long roomId;

    private String content;

    public static ChatMessage of(String sender, Long roomId, String content) {
        return ChatMessage.builder()
                .sender(sender)
                .roomId(roomId)
                .content(content)
                .build();
    }
}
