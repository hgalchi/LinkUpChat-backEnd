package LinkUpTalk.chat.presentation;

import LinkUpTalk.chat.application.SocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
public class WebsocketController {

    private final SimpMessagingTemplate template;
    private final SocketService socketService;



    // todo : 초기데이터 전송
    /*@SubscribeMapping(value = "/chat/room/{roomId}/data")
    public String subscribe(StompHeaderAccessor accessor, @DestinationVariable Long roomId, @Header("simpSessionAttributes") Map<String, Object> attributes) {

        String email = Optional.of((String) accessor.getSessionAttributes().get("email"))
                .orElseThrow(() -> new MessageDeliveryException("Invalid email"));

        //초기데이터 전송
        //return socketService.enterRoom(email, roomId);
        return "초기데이터 전송";
    }*/

    //브로드캐스팅
    /*@MessageMapping(value = "/room/{roomId}/message")
    public void message(@Payload ChatMessageDto message,
                        @DestinationVariable Long roomId,
                        Principal principal) {

        String email = principal.getName();
        if (message.getImageBinaryDate() == null) {
            socketService.saveMessage(message, roomId, email);
        } else {
            socketService.saveMessageWithImage(message, roomId, email);
        }
        template.convertAndSend("/topic/room/" + roomId, message);

    }

    //todo : 401 인증실패, 404 인가 실패
    //todo : 1:1 채팅
    @MessageMapping("/private")
    public void sendSpecific(ChatMessageDto message, @Header("simpSessionAttributes") Map<String, Object> sessionAttributes) {
        String sender=(String)sessionAttributes.get("sender");
        log.info(sender+"to "+message.getDestinationUser()+"chatting....");
        template.convertAndSendToUser(message.getDestinationUser(), "/queue/message", message.getMessage());
    }*/
}
