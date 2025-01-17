package LinkUpTalk.chat.presentation.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import LinkUpTalk.chat.application.ChatService;
import LinkUpTalk.chat.presentation.handler.JwtChannelInterceptor;
import LinkUpTalk.common.exception.BusinessException;
import LinkUpTalk.common.response.ResponseCode;
import LinkUpTalk.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    private ChatService chatServcie;

    @Mock
    private MessageChannel messageChannel;

    @Test
    @Tag("webSocketConnect")
    @DisplayName("웹 소켓 연결")
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

    @Test
    @DisplayName("test")
    void test(){
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.MESSAGE);
        accessor.setLeaveMutable(true);
        Message<?> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
        Message<?> result = jwtChannelInterceptor.preSend(message, messageChannel);
        System.out.println("result:"+result);
    }

    @Test
    @Tag("webSocketConnect")
    @DisplayName("웹 소켓 연결 실패_유효하지 않은 토큰")
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

    @Test
    void subscribe_validPath(){
        //given
        String email = "test@example.com";
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setDestination("/topic/room/123");
        accessor.setUser(new UsernamePasswordAuthenticationToken(email, null));
        Message<?> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        //when
        Message<?> result = jwtChannelInterceptor.preSend(message, messageChannel);

        //then
        assertNotNull(result);
        verify(chatServcie, Mockito.times(1)).join(email, 123L);
    }


}

