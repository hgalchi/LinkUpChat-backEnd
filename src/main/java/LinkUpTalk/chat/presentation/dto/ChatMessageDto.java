package LinkUpTalk.chat.presentation.dto;

import LinkUpTalk.chat.domain.constant.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ChatMessageDto {
    //private String destinationUser;
    private String message;
    private String sender;
    private MessageType messageType;

    //private String userConnection;

}
