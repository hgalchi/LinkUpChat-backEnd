package LinkUpTalk.chat.presentation.handler;

import LinkUpTalk.chat.domain.constant.MessageType;
import LinkUpTalk.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class WebSocketGlobalExceptionHandler {

    private final SimpMessagingTemplate template;
    private static final String ERROR_DESTINATION = "/queue/errors";

    @MessageExceptionHandler(BusinessException.class)
    public void handleBusinessException(Principal principal, @Payload String destination, BusinessException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("type", MessageType.ERROR.name());
        body.put("reason", ex.getMessage());
        //ErrorMessage errorMessge=ErrorMessage.of();
        template.convertAndSendToUser(principal.getName(), ERROR_DESTINATION,body);
    }
}
