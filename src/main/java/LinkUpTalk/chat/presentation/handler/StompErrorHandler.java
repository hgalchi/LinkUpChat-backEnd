package LinkUpTalk.chat.presentation.handler;

import LinkUpTalk.common.exception.BusinessException;
import LinkUpTalk.common.response.ResponseCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.log4j.Log4j2;
import org.apache.coyote.Response;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.security.SignatureException;

@Component
@Log4j2
public class StompErrorHandler extends StompSubProtocolErrorHandler {

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {

        log.error("Stomp Error Handler 인증 오류");
        Throwable cause = ex.getCause();
        if (cause instanceof BusinessException) {
            ResponseCode responseCode = ((BusinessException) cause).getErrorcode();
            return errorMessage(responseCode);
        }
        return super.handleClientMessageProcessingError(clientMessage, ex);
    }

    private Message<byte[]> errorMessage(ResponseCode code) {
        log.error("[connect] Exception cause : {}", code.getMessage());

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setMessage(String.valueOf(code.getStatus()));
        accessor.setLeaveMutable(true);

        return MessageBuilder.createMessage(code.getMessage().getBytes(StandardCharsets.UTF_8), accessor.getMessageHeaders());
    }
}
