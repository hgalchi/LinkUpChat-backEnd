package LinkUpTalk.chat.presentation.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import LinkUpTalk.annotation.UnitTest;
import LinkUpTalk.chat.application.ChatService;
import LinkUpTalk.common.exception.BusinessException;
import LinkUpTalk.common.response.ResponseCode;
import LinkUpTalk.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@ExtendWith(MockitoExtension.class)
public class JwtChannelInterceptorTest {

    @InjectMocks
    private JwtChannelInterceptor jwtChannelInterceptor;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private ChatService chatService;

    @Mock
    private MessageChannel messageChannel;

    @UnitTest
    @DisplayName("유효한 JWT 토큰으로 WebSocket 연결 시 인증 성공")
    void connect_validToken() {
        // Given
        String validToken = "Bearer valid.jwt.token";
        String email = "test@example.com";
        Claims claims = mock(Claims.class);

        UserDetails userDetails = User.builder()
                .username(email)
                .password("")
                .roles("CUSTOMER")
                .build();

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setNativeHeader("Authorization", validToken);
        accessor.setLeaveMutable(true);

        Message<?> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        when(jwtUtil.validateToken(anyString())).thenReturn(claims);
        when(jwtUtil.getEmail(claims)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        // When
        Message<?> result = jwtChannelInterceptor.preSend(message, messageChannel);

        // Then
        StompHeaderAccessor resultAccessor = StompHeaderAccessor.wrap(result);
        assertNotNull(result);
        assertNotNull(resultAccessor.getUser());
        assertEquals(email, resultAccessor.getUser().getName());
    }

    @UnitTest
    @DisplayName("유효하지 않은 JWT 토큰으로 WebSocket 연결 시 인증 실패")
    void connect_failWithExpiredToken(){
        // Given
        String expiredToken = "Bearer expired.jwt.token";

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setNativeHeader("Authorization", expiredToken);
        accessor.setLeaveMutable(true);

        Message<?> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        when(jwtUtil.validateToken(anyString())).thenThrow(new BusinessException(ResponseCode.EXPIRED_JWT));

        // When & Then
        assertThrows(BusinessException.class, () -> jwtChannelInterceptor.preSend(message, messageChannel));
    }
}

