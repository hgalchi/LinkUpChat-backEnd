package LinkUpTalk.chat.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ChatMessageDmResDto {
    String receiver;
    String sender;
    Long roomId;
    String content;

}
