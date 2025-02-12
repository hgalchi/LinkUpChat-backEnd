package LinkUpTalk.chat.presentation.event;

import LinkUpTalk.annotation.UnitTest;
import LinkUpTalk.chat.application.ChatService;
import LinkUpTalk.chat.domain.constant.MessageType;
import LinkUpTalk.chat.presentation.dto.ChatMessageReqDto;
import LinkUpTalk.chat.presentation.dto.ChatMessageResDto;
import LinkUpTalk.common.response.ResponseCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WebSocketEventListenerTest {

    @InjectMocks
    private WebSocketEventListener webSocketEventListener;

    @Mock
    private ChatService socketService;

    @Mock
    private RedisPublisher publisher;
    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private static final String EMAIL = "user@naver.com";
    private static final String SESSION_ID = "user-session-123";
    private static final String DESTINATION = "/topic/chat/group/123";

    @UnitTest
    @DisplayName("WebSocket 세션 연결 시 연결 이벤트를 처리한다. ")
    void handleSocketConnectListener_ShouldLogConnection() {
        // Given
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setUser(new UsernamePasswordAuthenticationToken(EMAIL, null));
        accessor.setSessionId(SESSION_ID);

        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(
                MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders())
        );
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], headerAccessor.getMessageHeaders());

        SessionConnectEvent event = new SessionConnectEvent(new Object(), message);

        // When
        assertDoesNotThrow(()->webSocketEventListener.handleSocketConnectListener(event));

    }

    @Nested
    @DisplayName("Session Subscribe")
    class subscribe{

        @UnitTest
        @DisplayName("WebSocket 세션 구독 시 구독 이벤트를 처리한다.")
        void subscribe_sessionEvent_suc() {
            //given
            StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
            accessor.setUser(new UsernamePasswordAuthenticationToken(EMAIL, null));
            accessor.setSessionId(SESSION_ID);
            accessor.setDestination(DESTINATION);

            SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(
                    MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders())
            );
            Message<byte[]> message = MessageBuilder.createMessage(new byte[0], headerAccessor.getMessageHeaders());

            SessionSubscribeEvent event = new SessionSubscribeEvent(new Object(), message);

            ChatMessageReqDto<Long> sendMessage = ChatMessageReqDto.<Long>builder()
                    .destination(123L)
                    .sender("producer")
                    .content("content")
                    .messageType(MessageType.GROUP_CHAT)
                    .build();

            Long roomId = 123L;
            when(socketService.join("user@naver.com", roomId)).thenReturn(sendMessage);

            webSocketEventListener.handleSocketSubscribeListener(event);

            verify(publisher, times(1)).sendMessage(sendMessage);
        }

        @UnitTest
        @DisplayName("Session 구독_잘못된 destination 설정")
        void subscribe_sessionEvent_failWithUnValidetedDestination(){
            //given
            StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
            accessor.setUser(new UsernamePasswordAuthenticationToken(EMAIL, null));
            accessor.setSessionId(SESSION_ID);

            SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(
                    MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders())
            );
            Message<byte[]> message = MessageBuilder.createMessage(new byte[0], headerAccessor.getMessageHeaders());

            SessionSubscribeEvent event = new SessionSubscribeEvent(new Object(), message);

            ChatMessageResDto res = ChatMessageResDto.builder()
                    .content(ResponseCode.STOMP_NOT_FOUND_DESTINATION.getMessage())
                    .messageType(MessageType.ERROR)
                    .build();

            webSocketEventListener.handleSocketSubscribeListener(event);

            verify(messagingTemplate, times(1)).convertAndSendToUser(eq(EMAIL),eq("/queue/errors"),refEq(res));
        }

    }

}