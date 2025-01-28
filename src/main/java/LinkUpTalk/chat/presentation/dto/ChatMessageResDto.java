package LinkUpTalk.chat.presentation.dto;

import LinkUpTalk.chat.domain.constant.MessageType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ChatMessageResDto {
    private String sender;
    private String content;
    private MessageType messageType;
}
