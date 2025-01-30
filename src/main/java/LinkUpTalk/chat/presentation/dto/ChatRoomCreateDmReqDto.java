package LinkUpTalk.chat.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ChatRoomCreateDmReqDto {
    Long receiverId;
    Long roomId;
    String content;
}
