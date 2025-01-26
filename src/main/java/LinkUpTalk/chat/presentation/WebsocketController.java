package LinkUpTalk.chat.presentation;

import LinkUpTalk.chat.application.ChatService;
import LinkUpTalk.chat.presentation.dto.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
public class WebsocketController {

    private final SimpMessagingTemplate template;
    private final ChatService chatService;

    //브로드캐스팅
    @MessageMapping(value = "/room/{roomId}/message")
    public void message(@Payload ChatMessageDto message,
                        @DestinationVariable Long roomId,
                        Principal principal) {

        String email = principal.getName();

        template.convertAndSend("/topic/room/" + roomId, message);

    }

    /*//todo : 401 인증실패, 404 인가 실패
    //todo : 1:1 채팅
    @MessageMapping("/private")
    public void sendSpecific(ChatMessageDto message, @Header("simpSessionAttributes") Map<String, Object> sessionAttributes) {
        String sender=(String)sessionAttributes.get("sender");
        log.info(sender+"to "+message.getDestinationUser()+"chatting....");
        template.convertAndSendToUser(message.getDestinationUser(), "/queue/message", message.getMessage());
    }*/
}
