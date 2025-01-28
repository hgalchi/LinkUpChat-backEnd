package LinkUpTalk.chat.presentation.dto;

import LinkUpTalk.chat.domain.constant.MessageType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ChatMessageReqDto<T> {
    private T destination;
    private String sender;
    private String content;
    private MessageType messageType;
}
