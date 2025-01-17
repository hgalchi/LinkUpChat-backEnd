package LinkUpTalk.chat.presentation.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebSocketJwtAuthTest {

    private WebSocketStompClient stompClient;

    @LocalServerPort
    private int port;

    private StompSession session;
    private String url;

    @BeforeEach
    void setup(){
        url = "ws://localhost:" + port + "/stomp";

        StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
        stompClient = new WebSocketStompClient(webSocketClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

    }

    @Test
    @DisplayName("웹소켓 연결 실패_유효하지 않은 토큰")
    void connect_failWithInvalidToken(){
        //Givne
        StompHeaders headers = new StompHeaders();
        headers.add("Authorization", "Bear invalid.");

        //when & Then
        assertThrows(ExecutionException.class, () -> {
            stompClient.connect(url, new WebSocketHttpHeaders(), headers, new StompSessionHandlerAdapter() {
                    })
                    .get(3, TimeUnit.SECONDS);
        });
    }

}