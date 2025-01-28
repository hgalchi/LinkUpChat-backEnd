package LinkUpTalk.chat.presentation.event;

import LinkUpTalk.chat.application.ChatService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@SpringBootTest
class WebSocketEventListenerTest {

    @Spy // ✅ 실제 객체를 감싸서 스파이 객체로 변경
    @InjectMocks
    private WebSocketEventListener socketEventListener;

    @Mock
    private ChatService socketService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Test
    @DisplayName("연결 이벤트 리스너로 비즈니스 로직 호출")
    void connectSessionEvent() {
        // given
        String email = "test@naver.com";
        String sessionId = "test-session-123";
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setUser(new UsernamePasswordAuthenticationToken(email, null));
        accessor.setSessionId(sessionId);

        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(
                MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders())
        );
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], headerAccessor.getMessageHeaders());

        // when
        SessionConnectEvent event = new SessionConnectEvent(new Object(), message);
    }

    @Test
    @DisplayName("구독 이벤트 리스너로 비즈니스 로직 호출 ")
    void subScribe_sessionEvent(){
        // given
        String email = "test@naver.com";
        String sessionId = "test-session-123";
        String destination = "/sub/room/123";
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setUser(new UsernamePasswordAuthenticationToken(email, null));
        accessor.setSessionId(sessionId);
        accessor.setDestination(destination);

        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(
                MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders())
        );
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], headerAccessor.getMessageHeaders());

        // when
        SessionSubscribeEvent event = new SessionSubscribeEvent(new Object(), message);
       // socketEventListener.handleSocketSubscribeListener(event);
        //then
        verify(socketService, times(1)).join(email, 123L);
        verify(messagingTemplate, times(1)).convertAndSend(destination, email + "님이 입장했습니다.");

    }
}