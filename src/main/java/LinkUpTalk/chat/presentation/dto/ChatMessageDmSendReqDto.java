package LinkUpTalk.chat.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Builder
@AllArgsConstructor
public class ChatMessageDmSendReqDto {
    Long receiverId;
    String content;
}
