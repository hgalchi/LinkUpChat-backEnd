package com.example.security.chat;

import com.example.security.chat.service.SocketService;
import com.example.security.user.repository.UserRepository;
import com.example.security.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.core.config.Order;
import org.springframework.core.Ordered;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 메세지가 채널로 전송되기 전 사전처리
 */
@Component
@Log4j2
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class ChatroomInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final SocketService socketService;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
            printStompFrame(message,accessor);

        /*
         *CONNECT할 경우 유저의 accessToken을 사용해 인증 여부를 확인 후
         * STOMP HEADER에 식별가능한 값 email을 추가
         */
        if (accessor.getCommand().equals(StompCommand.CONNECT)) {
            //token 인증
            String email =validateToken(accessor);

            //header에 정보추가
            //동일한 세션으로 전송된 메세지 헤더에는 socketContext에서 관리된다.
            //1. simpUser
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            UsernamePasswordAuthenticationToken authentication=
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            accessor.setUser(authentication);
            accessor.getUser().getName();

            //2. simpSessionAttributes
            Map<String,Object> sessionAttributes= accessor.getSessionAttributes();
            String username = userRepository.findByEmail(email).get().getName();
            assert sessionAttributes != null;
            sessionAttributes.put("name", username);
        /*
         * SUBCRIBE할 경우 구독 경로를 검사한 후
         * 채팅방에 유저 추가
         */
        }else if(accessor.getCommand().equals(StompCommand.SUBSCRIBE)){
            //destination 경로 검사 "/app/chat/room/**","/user/queue**"이외의 경로 구독은 거절
            String destination = accessor.getDestination();
            if (destination == null || (!destination.startsWith("/topic/room/") && !destination.startsWith("/user/queue/"))) {
                throw new MessageDeliveryException("invalid destination");
            }
            //방인원 검사
            if(destination.startsWith("/topic/room/")){
                String email=Optional.of(accessor.getUser().getName())
                        .orElseThrow(()->new MessageDeliveryException("Invalid email"));
                Long roomId = Long.parseLong(destination.split("/")[3]) ;
                socketService.join(email,roomId);
            }
        /*
         * 클라이언트의 MESSAGE는 모두 거절
         */
        }else if(accessor.getCommand().equals(StompCommand.MESSAGE)){
            throw new MessageDeliveryException("Invalid command");
        }


        return message;
    }
    /**
     * 토큰 인증
     * 만료나 변조 시, 예외를 터트린다.
     * return 토큰 인증 성공 시 사용자의 이메일을 반환한다.
     */
    //todo : jwtUtil에서 exceptio처리 정리하기
    private String  validateToken(StompHeaderAccessor accessor) {

        String authorizationHeader = String.valueOf(accessor.getNativeHeader("Authorization").get(0));

        String token = resolveAuthorizationHeader(authorizationHeader).trim();
        try {
            Claims claims = Optional.ofNullable(jwtUtil.tokenReverseClaims(token))
                    .orElseThrow(() -> new MessageDeliveryException("to 유효기간이 만료되었습니다."));
            return jwtUtil.getEmail(claims);
        }catch (Exception e){
            throw new MessageDeliveryException("token이나 cliams의 유효기간 만료");
        }
    }

    private String resolveAuthorizationHeader(String authorizationHeader) {
        String token = Optional.ofNullable(jwtUtil.resolveToken(authorizationHeader))
                .orElseThrow(() -> new MessageDeliveryException("Authorization header is miassing"));
        return token;


    }

    /**
     * 요청 Frame출력
     */
    private void printStompFrame(Message<?> message,StompHeaderAccessor accessor) {
        log.info("==================================================");
        log.info("Command: " + accessor.getCommand());
        MessageHeaders headers = message.getHeaders();
        MultiValueMap<String, String> multiValueMap = headers.get(StompHeaderAccessor.NATIVE_HEADERS, MultiValueMap.class);
        if (multiValueMap != null) {
            for (Map.Entry<String, List<String>> head : multiValueMap.entrySet()) {
                log.info(head.getKey() + "#" + head.getValue());
            }
        }
        log.info("==================================================");

    }
}
