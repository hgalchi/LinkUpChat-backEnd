package LinkUpTalk.chat.presentation;

import LinkUpTalk.chat.application.SocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.*;
// todo : 채팅방 데이터는 sql 채팅 메시지는 noSql

@Component
@RequiredArgsConstructor
@Log4j2
public class SocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final SocketService socketService;

    @EventListener
    public void handleSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String email = accessor.getUser().getName();
        log.info("{} 사용자 연결",email);
    }

    //sendtouser로 초기데이터 보내는 작업 추가
    @EventListener
    public void handleSocketSubscribeListener(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = accessor.getDestination();
        String email = accessor.getUser().getName();
        Long roomId = Long.parseLong(destination.split("/")[3]);

        log.info("{} 사용자 {} 채팅방 입장",email,roomId);

        //그룹채팅 구독
        if(destination.startsWith("/sub/room/")) {
            socketService.join(email, roomId);
            messagingTemplate.convertAndSend(destination, email + "님이 입장했습니다.");
        }
    }




 /*   @EventListener
    public void handleUnSubscribeEvent(SessionUnsubscribeEvent event) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        log.info("==============unsubscribe 구독요청");
        messagingTemplate.convertAndSend(accessor.getDestination(), name + "이 채팅방을 나갔습니다.");
    }


    @EventListener
    public void handleWebSocketDisconnectListner(SessionDisconnectEvent event) {
        log.info("disconnect 연결끊김");

                messagingTemplate.convertAndSend(subscription,getOnlineRoomUsers(subscription));
        }

    }*/

 /*   private String getUserName(AbstractSubProtocolEvent event) {
        Map simpSessionAttributes = (Map) event.getMessage().getHeaders().get("simpSessionAttributes");
        return simpSessionAttributes.get("name").toString();
    }

    private void addUserSubscribed(String name, String subscribedChannel) {
        Set<String> subscriptions = userToSubscriptionId.getOrDefault(name, new HashSet<>());
        subscriptions.add(subscribedChannel);
        userToSubscriptionId.put(name, subscriptions);
    }*/

}
