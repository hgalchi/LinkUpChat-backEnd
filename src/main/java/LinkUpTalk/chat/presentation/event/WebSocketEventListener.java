package LinkUpTalk.chat.presentation.event;

import LinkUpTalk.chat.application.ChatService;
import LinkUpTalk.chat.domain.constant.MessageType;
import LinkUpTalk.chat.presentation.dto.ChatMessageReqDto;
import LinkUpTalk.chat.presentation.dto.ChatMessageResDto;
import LinkUpTalk.common.exception.BusinessException;
import LinkUpTalk.common.response.ErrorResponse;
import LinkUpTalk.common.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.*;

import java.security.Principal;
import java.util.Objects;
import java.util.Optional;
// evnetListeneer-> controller로 변경할지 생각해보기

@Component
@RequiredArgsConstructor
@Log4j2
public class WebSocketEventListener {

    private final ChatService socketService;
    private final RedisTemplate<String,Object> redisTemplate;
    private final SimpMessagingTemplate messageTemplate;
    private static final String CHANNEL_TOPIC = "chatroom";

    @EventListener
    public void handleSocketConnectListener(SessionConnectEvent event) {
        log.info("WebSocket Connect");
        StompHeaderAccessor accessor = getStompHeaderAccessor(event);
        String email = accessor.getUser().getName();

        log.info("{} 사용자 연결",email);
    }

    @EventListener
    public void handleSocketSubscribeListener(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String email = getEmail(accessor);
        try {
            String destination = getDestination(accessor);
            log.info("{} 사용자 {} 채팅방 구독", email, destination);

            if(destination.startsWith("/topic/chat/group")) {
                Long roomId = parseRoomId(destination);
                ChatMessageReqDto message=socketService.join(email, roomId);
                redisTemplate.convertAndSend(CHANNEL_TOPIC, message);
            //todo : event 비동기 처리 exception
            }
        }catch (BusinessException ex) {
            log.error("[subscribe] Exception Cause: {}", ex.getMessage());
            ChatMessageResDto res = ChatMessageResDto.builder()
                    .content(ex.getMessage())
                    .messageType(MessageType.ERROR)
                    .build();
            messageTemplate.convertAndSendToUser(email, "/queue/errors",res);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListner(SessionDisconnectEvent event) {
        log.info("{} 사용자 연결 끊김",event.getSessionId());
    }

    private StompHeaderAccessor getStompHeaderAccessor(SessionConnectEvent event){
        return StompHeaderAccessor.wrap(event.getMessage());
    }

    private String getDestination(StompHeaderAccessor accessor) {
        return Optional.ofNullable(accessor.getDestination())
                .orElseThrow(() -> new BusinessException(ResponseCode.STOMP_NOT_FOUND_DESTINATION));
    }

    //todo : of와 ofnullable 비교
    private String getEmail(StompHeaderAccessor accessor) {
        return accessor.getUser().getName();
    }

    private Long parseRoomId(String destination) {
        return Long.parseLong(destination.split("/")[4]);
    }

}
