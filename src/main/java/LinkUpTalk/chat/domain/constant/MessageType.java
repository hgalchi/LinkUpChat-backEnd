package LinkUpTalk.chat.domain.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MessageType {
    CHAT,
    JOIN,
    LEAVE,
    ERROR
}
//CHAT,JOIN,FRIEND_ONLINE,FRIEND_OFFLINE

