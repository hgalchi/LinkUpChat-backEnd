package LinkUpTalk.chat.domain;

import LinkUpTalk.chat.domain.constant.MessageType;
import LinkUpTalk.chat.presentation.dto.ChatMessageReqDto;
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

    private MessageType messageType;

    public static ChatMessage of(String sender, Long roomId, String content,MessageType messageType) {
        return ChatMessage.builder()
                .sender(sender)
                .roomId(roomId)
                .content(content)
                .messageType(messageType)
                .build();
    }

    public ChatMessageReqDto toChatMessageReqDto() {
        return ChatMessageReqDto.builder()
                .sender(sender)
                .content(content)
                .messageType(messageType)
                .destination(roomId)
                .build();
    }

}
