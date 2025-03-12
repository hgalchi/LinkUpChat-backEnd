package LinkUpTalk.chat.presentation;

import LinkUpTalk.chat.application.ChatService;
import LinkUpTalk.chat.presentation.dto.ChatMessageDmSendReqDto;
import LinkUpTalk.chat.presentation.dto.ChatMessageReqDto;
import LinkUpTalk.chat.presentation.event.RedisPublisher;
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
    private final RedisPublisher publisher;

    @MessageMapping(value = "/chat/group/{roomId}")
    public void message(@Payload String message,
                        @DestinationVariable Long roomId,
                        Principal principal) {
        log.info("webSocket Send : {} 사용자가 {}그룹 채팅방에 메시지를 전송",principal.getName(),roomId);

        publisher.sendMessage(chatService.saveGroupMessage(principal.getName(), roomId, message));
    }

    @MessageMapping("/chat/dm/{roomId}")
    public void sendSpecific(@Payload ChatMessageDmSendReqDto req,
                             @DestinationVariable Long roomId,
                             Principal principal) {
        log.info("webSocket Send : {} 사용자가 {}개인 채팅방에 메시지를 전송",principal.getName(),roomId);

        publisher.sendMessage(chatService.saveDmMessage(principal.getName(),roomId,req));
    }
}
