package LinkUpTalk.chat;

import LinkUpTalk.annotation.IntegrateTest;
import LinkUpTalk.chat.config.IntegrationTest;
import LinkUpTalk.chat.domain.constant.MessageType;
import LinkUpTalk.chat.presentation.dto.ChatMessageDmSendReqDto;
import LinkUpTalk.chat.presentation.dto.ChatMessageResDto;
import LinkUpTalk.util.TestUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_CLASS;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@Sql(scripts = "/websocket-test-data.sql", executionPhase = BEFORE_TEST_CLASS)
class WebSocketChatIntegrationTest extends IntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestUtil testUtil;

    private WebSocketStompClient stompClient;
    private String url;
    private String producerValidToken;
    private String receiverValidToken;

    private static final String PRODUCER_NAME = "producer";
    private static final String RECEIVER_NAME = "receiver";
    private static final String PRODUCER_EMAIL = "producer@naver.com";
    private static final String RECEIVER_EMAIL = "receiver@naver.com";
    private static final Long GROUP_CHATROOM_ID = 1L;
    private static final Long DM_CHATROOM_ID = 2L;
    private static final Long RECEIVER_ID = 2L;

    @BeforeEach
    void setup() {
        url = "ws://localhost:" + port + "/stomp";

        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        producerValidToken = testUtil.getToken(PRODUCER_EMAIL);
        receiverValidToken = testUtil.getToken(RECEIVER_EMAIL);
    }

    @IntegrateTest
    @DisplayName("WebSocket 연결 및 인증 성공")
    void connect_suc() throws ExecutionException, InterruptedException, TimeoutException {
        //given
        StompHeaders headers = new StompHeaders();
        headers.add("Authorization", producerValidToken);

        //when
        StompSession session = sessionConnect(url, headers);

        //then
        assertThat(session).isNotNull();
        assertThat(session.isConnected()).isTrue();
    }


    @IntegrateTest
    @DisplayName("WebSocket 그룹 채팅방 구독 및 순차적인 입장 메시지 수신 성공")
    void subscribe_suc_sequentially() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        StompHeaders headers1 = new StompHeaders();
        headers1.add("Authorization", producerValidToken);
        StompHeaders headers2 = new StompHeaders();
        headers2.add("Authorization", receiverValidToken);

        // WebSocket 세션 생성
        StompSession producerSession = sessionConnect(url, headers1);
        StompSession receiverSession = sessionConnect(url, headers2);


        // 메시지 대기 큐B
        BlockingDeque<ChatMessageResDto> messageQueue1 = new LinkedBlockingDeque<>();
        BlockingDeque<ChatMessageResDto> messageQueue2 = new LinkedBlockingDeque<>();

        ChatMessageResDto expected1 = ChatMessageResDto.builder().messageType(MessageType.JOIN).sender(PRODUCER_NAME).content(PRODUCER_NAME + " 님이 입장했습니다.").build();
        ChatMessageResDto expected2 = ChatMessageResDto.builder().messageType(MessageType.JOIN).sender(RECEIVER_NAME).content(RECEIVER_NAME + " 님이 입장했습니다.").build();

        String subDestination = "/topic/chat/group/" + GROUP_CHATROOM_ID;

        // When
        subScribeToTopic(producerSession, subDestination, messageQueue1);
        Thread.sleep(500);
        subScribeToTopic(receiverSession, subDestination, messageQueue2);

        ChatMessageResDto res1 = messageQueue1.poll(3, TimeUnit.SECONDS);
        ChatMessageResDto res2 = messageQueue2.poll(3, TimeUnit.SECONDS);

        // Then
        assertThat(res1).isNotNull();
        assertThat(res1.getContent()).isEqualTo(expected1.getContent());
        assertThat(res1.getSender()).isEqualTo(expected1.getSender());
        assertThat(res1.getMessageType()).isEqualTo(expected1.getMessageType());

        assertThat(res2).isNotNull();
        assertThat(res2.getContent()).isEqualTo(expected2.getContent());
        assertThat(res2.getSender()).isEqualTo(expected2.getSender());
        assertThat(res2.getMessageType()).isEqualTo(expected2.getMessageType());
    }


    @IntegrateTest
    @DisplayName("WebSocket 그룹 채팅 메시지 전송 및 수신 성공")
    void groupChatSend_suc() throws InterruptedException, ExecutionException, TimeoutException {
        // Given
        StompHeaders headers1 = new StompHeaders();
        headers1.add("Authorization", producerValidToken);
        StompHeaders headers2 = new StompHeaders();
        headers2.add("Authorization", receiverValidToken);

        // WebSocket 세션 생성
        StompSession session1 = sessionConnect(url, headers1);
        StompSession session2 = sessionConnect(url, headers2);


        // 메시지 대기 큐
        BlockingDeque<ChatMessageResDto> messageQueue1 = new LinkedBlockingDeque<>();
        BlockingDeque<ChatMessageResDto> messageQueue2 = new LinkedBlockingDeque<>();

        String subDestination = "/topic/chat/group/" + GROUP_CHATROOM_ID;

        //topic 구독
        subScribeToTopic(session1, subDestination, messageQueue1);
        subScribeToTopic(session2, subDestination, messageQueue2);

        // When
        session1.send("/pub/chat/group/" + GROUP_CHATROOM_ID, "hi");

        System.out.println("입장 메시지 :" + messageQueue2.take());
        System.out.println("입장 메시지 :" + messageQueue2.take());
        ChatMessageResDto res = messageQueue2.take();

        // Then
        assertThat(res.getSender()).isEqualTo(PRODUCER_NAME);
        assertThat(res.getContent()).isEqualTo("hi");
        assertThat(res.getMessageType()).isEqualTo(MessageType.GROUP_CHAT);
    }

    @IntegrateTest
    @DisplayName("WebSocket 개인 채팅 메시지 전송 및 수신 성공")
    void dmChatSend_suc() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        StompHeaders headers1 = new StompHeaders();
        headers1.add("Authorization", producerValidToken);
        StompHeaders headers2 = new StompHeaders();
        headers2.add("Authorization", receiverValidToken);

        // WebSocket 세션 생성
        StompSession session1 = sessionConnect(url, headers1);
        StompSession session2 = sessionConnect(url, headers2);


        // 메시지 대기 큐
        BlockingDeque<ChatMessageResDto> messageQueue1 = new LinkedBlockingDeque<>();
        BlockingDeque<ChatMessageResDto> messageQueue2 = new LinkedBlockingDeque<>();

        ChatMessageDmSendReqDto sendDmMessage = ChatMessageDmSendReqDto.builder().receiverId(RECEIVER_ID).content("hi").build();

        String subDestination = "/user/queue/chat";

        //topic 구독
        subScribeToTopic(session1, subDestination, messageQueue1);
        Thread.sleep(1000);
        subScribeToTopic(session2, subDestination, messageQueue2);


        // When
        session1.send("/pub/chat/dm/" + DM_CHATROOM_ID, sendDmMessage);

        ChatMessageResDto res = messageQueue2.poll(3, TimeUnit.SECONDS);


        assertThat(res.getSender()).isEqualTo(PRODUCER_NAME);
        assertThat(res.getContent()).isEqualTo("hi");
        assertThat(res.getMessageType()).isEqualTo(MessageType.DM_CHAT);
    }

    private StompSession sessionConnect(String url, StompHeaders header) throws ExecutionException, InterruptedException, TimeoutException {
        return stompClient.connect(url, new WebSocketHttpHeaders(), header, new StompSessionHandlerAdapter() {
        }).get(3, TimeUnit.SECONDS);
    }

    private void subScribeToTopic(StompSession session, String subDestination, BlockingQueue<ChatMessageResDto> messageQueue) throws ExecutionException, InterruptedException, TimeoutException {
        session.subscribe(subDestination, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ChatMessageResDto.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                messageQueue.offer((ChatMessageResDto) payload);
            }
        });
    }

}