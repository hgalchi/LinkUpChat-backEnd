package LinkUpTalk.chat.presentation;

import LinkUpTalk.chat.application.ChatService;
import LinkUpTalk.chat.presentation.dto.ChatMessageDmSendReqDto;
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


    @MessageMapping(value = "/chat/group/{roomId}")
    public void message(@Payload String message,
                        @DestinationVariable Long roomId,
                        Principal principal) {
        log.info("webSocket Send : {} 사용자가 {}그룹 채팅방에 메시지를 전송",principal.getName(),roomId);

        ChatMessageReqDto groupMessage = chatService.saveGroupMessage(principal.getName(), roomId, message);
        redisTemplate.convertAndSend(CHANNEL_TOPIC,groupMessage);
    }

    @MessageMapping("/chat/dm/{roomId}")
    public void sendSpecific(@Payload ChatMessageDmSendReqDto req,
                             @DestinationVariable Long roomId,
                             Principal principal) {
        log.info("webSocket Send : {} 사용자가 {}개인 채팅방에 메시지를 전송",principal.getName(),roomId);

        ChatMessageReqDto dmMessage=chatService.saveDmMessage(principal.getName(),roomId,req);
        redisTemplate.convertAndSend(CHANNEL_TOPIC, dmMessage);
    }
}
