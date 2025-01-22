package LinkUpTalk.chat.presentation.handler;

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

import static org.springframework.messaging.simp.stomp.StompCommand.CONNECT;

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

        if (accessor.getCommand().equals(CONNECT)) {
            log.info("channelInterceptor connect");
            String email =getEmailAndValidateToken(accessor);
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
//            /*UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(email)
//                    .password("password")
//                    .build();*/

            UsernamePasswordAuthenticationToken authentication=
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            accessor.setUser(authentication);
        }

        return message;
    }

    private String  getEmailAndValidateToken(StompHeaderAccessor accessor) {
        Optional<String> jwtTokenOptional = Optional.ofNullable(accessor.getFirstNativeHeader(AUTHORIZATION));
        Claims claims = jwtTokenOptional
                .filter(token -> token.startsWith(BEARER))
                .map(token -> token.substring(BEARER.length()))
                .map(token->jwtUtil.validateToken(token))
                .orElseThrow(() -> new BusinessException(ResponseCode.MALFORMED_JWT));
        return jwtUtil.getEmail(claims);
    }
}
