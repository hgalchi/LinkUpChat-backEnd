package LinkUpTalk.chat.presentation.handler;

import LinkUpTalk.common.response.ResponseCode;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.nio.charset.StandardCharsets;

@Component
@Log4j2
public class StompExceptionHandler extends StompSubProtocolErrorHandler {

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        log.error("Stomp ExceptionHandler 호출");
        if (ex instanceof MessageDeliveryException) {
            Throwable cause = ex.getCause();
            log.error("Stomp Exception Handler :"+cause);
        }
        if (ex.getCause().getMessage().equals("UNAUTHORIZED")) {
            return errorMessage(ResponseCode.UNAUTHORIZED);
        }
        if (ex.getCause().getMessage().equals("INVALID_DESTINATION")) {
            return errorMessage(ResponseCode.INVALID_DESTINATION);
        }
        log.info("CustomErrorHandler exception : " + ex);

        return super.handleClientMessageProcessingError(clientMessage, ex);

    }

    private Message<byte[]> errorMessage(ResponseCode code) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);

        String message = String.valueOf(code.getMessage());
        accessor.setMessage(String.valueOf(code.getStatus()));
        accessor.setLeaveMutable(true);

        return MessageBuilder.createMessage(message.getBytes(StandardCharsets.UTF_8), accessor.getMessageHeaders());
    }

}
