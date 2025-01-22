package LinkUpTalk.chat.presentation.handler;

import LinkUpTalk.auth.domain.constant.TokenType;
import LinkUpTalk.common.exception.BusinessException;
import LinkUpTalk.common.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebSocketJwtAuthTest {


    @LocalServerPort
    private int port;

    @Autowired
    private JwtUtil jwtUtil;

    private WebSocketStompClient stompClient;
    private String url;
    private String validToken;

    @BeforeEach
    void setup() {
        url = "ws://localhost:" + port + "/stomp";

        StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
        stompClient = new WebSocketStompClient(webSocketClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        validToken = jwtUtil.createToken("wywudi@naver.com", List.of(), TokenType.accessToken.name());

    }

    @Test
    @Tag("webSocketConnect")
    @DisplayName("연결 성공")
    void connect_suc() throws ExecutionException, InterruptedException, TimeoutException {
        //given
        StompHeaders headers = new StompHeaders();
        headers.add("Authorization", validToken);

        //when
        StompSession session = stompClient.connect(url, new WebSocketHttpHeaders(), headers, new StompSessionHandlerAdapter() {
        }).get(3, TimeUnit.SECONDS);

        //then
        assertNotNull(session);
        assertTrue(session.isConnected());
    }

    @Test
    @Tag("webSocketConnect")
    @DisplayName("연결 실패_유효하지 않은 토큰")
    void connect_failWithInvalidToken() {
        //given
        StompHeaders headers = new StompHeaders();

        //when & Then
        assertThrows(BusinessException.class, () -> {
            stompClient.connect(url, new WebSocketHttpHeaders(), headers, new StompSessionHandlerAdapter() {
                    })
                    .get(3, TimeUnit.SECONDS);
        });
    }

    @Test
    @Tag("webSocketSubscribe")
    @DisplayName("구독자에게 메시지를 브로드 캐스팅 성공")
    void subscribe_suc() throws ExecutionException, InterruptedException, TimeoutException {
        //given

        StompHeaders headers = new StompHeaders();
        headers.add("Authorization", VALID_TOKEN);

        //when
        StompSession session = stompClient.connect(url, new WebSocketHttpHeaders(), headers, new StompSessionHandlerAdapter() {
        }).get(3, TimeUnit.SECONDS);

        String destination = "/topic/room/3";
        BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();

        session.subscribe(destination, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                messageQueue.offer((String) payload);
            }
        });



    }
}