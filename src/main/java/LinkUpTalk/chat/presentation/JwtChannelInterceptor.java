package LinkUpTalk.chat.presentation;

import LinkUpTalk.common.response.ResponseCode;
import LinkUpTalk.common.exception.BusinessException;
import LinkUpTalk.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.core.config.Order;
import org.springframework.core.Ordered;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 메세지가 채널로 전송되기 전 사전처리
 */
@Component
@Log4j2
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class JwtChannelInterceptor implements ChannelInterceptor {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || accessor.getCommand() == null) {
            throw new MessageDeliveryException("Invalid STOMP frame");
        }

        switch(accessor.getCommand()) {
            case CONNECT:
                handleConnect(accessor);
                break;
            case SUBSCRIBE:
                handleSubscribe(accessor);
                break;
            case MESSAGE:
                throw new MessageDeliveryException("Invalid command");
            default:
                break;
        }
        return message;
    }

    /**
     * 토큰 인증
     * 만료나 변조 시, 예외를 터트린다.
     * return 토큰 인증 성공 시 사용자의 이메일을 반환한다.
     */
    //todo : jwtUtil에서 exceptio처리 정리하기
    private void handleConnect(StompHeaderAccessor accessor) {
        String email =getEmailAndValidateToken(accessor);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken authentication=
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        accessor.setUser(authentication);
    }

    private void handleSubscribe(StompHeaderAccessor accessor) {
        String destination = Optional.ofNullable(accessor.getDestination())
                .orElseThrow(() -> new MessageDeliveryException("Missing destination"));
        checkDestination(destination);
    }
    private String  getEmailAndValidateToken(StompHeaderAccessor accessor) {
        Optional<String> jwtTokenOptional = Optional.ofNullable(accessor.getFirstNativeHeader(AUTHORIZATION));
        String jwtToken = jwtTokenOptional
                .filter(token -> token.startsWith(BEARER))
                .map(token -> token.substring(BEARER.length()))
                .orElseThrow(() -> new BusinessException(ResponseCode.MALFORMED_JWT));
        Claims claims=jwtUtil.validateToken(jwtToken);
        return jwtUtil.getEmail(claims);
    }

    private void checkDestination(String destination) {
        if (destination == null || (!destination.startsWith("/topic/room/") && !destination.startsWith("/user/queue/"))) {
            throw new MessageDeliveryException("invalid destination");
        }
    }
}
