package LinkUpTalk.chat.presentation;

import LinkUpTalk.chat.application.ChatService;
import LinkUpTalk.chat.presentation.dto.ChatMessageDmReqDto;
import LinkUpTalk.chat.presentation.dto.ChatMessageReqDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Log4j2
public class ChatController {

    private final ChatService chatService;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CHANNEL_TOPIC = "chatroom";


    //todo : dto 클래스 만들기
    @MessageMapping(value = "/chat/room/{roomId}")
    public void message(@Payload String message,
                        @DestinationVariable Long roomId,
                        Principal principal) {
        log.info("webSocket Send : {} 사용자가 {}채팅방에 메시지를 전송",principal.getName(),roomId);

        ChatMessageReqDto roomMessage = chatService.saveMessage(principal.getName(), roomId, message);

        redisTemplate.convertAndSend(CHANNEL_TOPIC, roomMessage);
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
