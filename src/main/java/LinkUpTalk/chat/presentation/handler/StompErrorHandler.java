package LinkUpTalk.chat.presentation.handler;

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
        if (ex instanceof MessageDeliveryException) {
            Throwable cause = ex.getCause();
            log.error("Stomp ExceptionHandler cause:" + cause);
            if (cause instanceof AccessDeniedException) {
                return errorMessage(ResponseCode.FORBIDDEN);
            }
            if (isJwtException(cause)) {
                return errorMessage(ResponseCode.SIGNATURE_JWT);
            }
        }
        return super.handleClientMessageProcessingError(clientMessage, ex);
    }

    private boolean isJwtException(Throwable ex) {
        return ex instanceof SignatureException
                || ex instanceof MalformedJwtException
                || ex instanceof ExpiredJwtException;
    }

    private Message<byte[]> errorMessage(ResponseCode code) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);

        String message = String.valueOf(code.getMessage());
        accessor.setMessage(String.valueOf(code.getStatus()));
        accessor.setLeaveMutable(true);

        return MessageBuilder.createMessage(message.getBytes(StandardCharsets.UTF_8), accessor.getMessageHeaders());
    }

}
