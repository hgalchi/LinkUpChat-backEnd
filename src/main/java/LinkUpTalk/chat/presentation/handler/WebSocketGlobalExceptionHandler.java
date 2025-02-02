package LinkUpTalk.chat.presentation.handler;

import LinkUpTalk.chat.domain.constant.MessageType;
import LinkUpTalk.chat.presentation.dto.ChatMessageResDto;
import LinkUpTalk.common.exception.BusinessException;
import LinkUpTalk.common.response.ErrorResponse;
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

    private static final String ERROR_DESTINATION = "/queue/errors";
    private final SimpMessagingTemplate template;

    @MessageExceptionHandler(BusinessException.class)
    public void handleBusinessException(Principal principal, BusinessException ex) {
        log.info("[send] Exception cause :{}", ex.getMessage());

        ChatMessageResDto res = ChatMessageResDto.builder()
                .content(ex.getMessage())
                .messageType(MessageType.ERROR)
                .build();
        template.convertAndSendToUser(principal.getName(), ERROR_DESTINATION,res);
    }
}
