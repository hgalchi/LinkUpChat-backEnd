package LinkUpTalk.chat.presentation.event;

import LinkUpTalk.chat.application.ChatService;
import LinkUpTalk.chat.presentation.dto.ChatMessageReqDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static reactor.core.publisher.Mono.when;

@SpringBootTest
@ActiveProfiles("test")
class WebSocketEventListenerTest {

    @Spy
    @InjectMocks
    private WebSocketEventListener socketEventListener;

    @Mock
    private ChatService socketService;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Test
    @DisplayName("연결 이벤트 리스너로 비즈니스 로직 호출")
    void connectSessionEvent() {
        // Given
        String email = "test@naver.com";
        String sessionId = "test-session-123";

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setUser(new UsernamePasswordAuthenticationToken(email, null));
        accessor.setSessionId(sessionId);

        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(
                MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders())
        );
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], headerAccessor.getMessageHeaders());

        SessionConnectEvent event = new SessionConnectEvent(new Object(), message);

        // When & Then
        assertDoesNotThrow(() -> socketEventListener.handleSocketConnectListener(event));
    }
}