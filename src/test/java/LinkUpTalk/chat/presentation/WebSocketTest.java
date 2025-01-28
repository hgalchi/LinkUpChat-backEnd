package LinkUpTalk.chat.presentation;

import LinkUpTalk.chat.domain.ChatRoom;
import LinkUpTalk.chat.domain.constant.ChatRoomType;
import LinkUpTalk.chat.domain.constant.MessageType;
import LinkUpTalk.chat.presentation.dto.ChatMessageDmSendReqDto;
import LinkUpTalk.chat.presentation.dto.ChatMessageResDto;
import LinkUpTalk.user.domain.User;
import LinkUpTalk.util.TestUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class WebSocketTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestUtil testUtil;

    private WebSocketStompClient stompClient;
    private String url;
    private String validToken1;
    private String validToken2;
    private User user1;
    private User user2;
    private ChatRoom chatRoom;

    @BeforeEach
    void setup() {
        url = "ws://localhost:" + port + "/stomp";

        stompClient = new WebSocketStompClient( new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        setUpTestData();
    }

    private void setUpTestData(){
        //todo : 왜 db입력 값으로 조회가 되지 않는지
        //todo : chatRoom을 repository로 조회해야함.
        chatRoom = testUtil.registerGroupChatRoom("test chatRoom", 10, ChatRoomType.GROUP);
        user1 = testUtil.registerUsers("user111@naver.com", "user1");
        user2 = testUtil.registerUsers("user222@naver.com", "user2");
        validToken1 = testUtil.getToken("user1@naver.com");
        validToken2 = testUtil.getToken("user2@naver.com");
    }


    @Test
    @Tag("webSocketConnect")
    @DisplayName("연결 성공")
    void connect_suc() throws ExecutionException, InterruptedException, TimeoutException {
        //given
        StompHeaders headers = new StompHeaders();
        headers.add("Authorization", validToken1);

        //when
        StompSession session = sessionConnect(url, headers);

        //then
        assertThat(session).isNotNull();
        assertThat(session.isConnected()).isTrue();
    }


    @Test
    @Tag("webSocketSubscribe")
    @DisplayName("구독 성공")
    void subscribe_suc_sequentially() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        StompHeaders headers1 = new StompHeaders();
        headers1.add("Authorization", validToken1);
        StompHeaders headers2 = new StompHeaders();
        headers2.add("Authorization", validToken2);

        // WebSocket 세션 생성
        StompSession session1 = sessionConnect(url, headers1);
        StompSession session2 = sessionConnect(url, headers2);


        // 메시지 대기 큐
        BlockingDeque<ChatMessageResDto> messageQueue1 = new LinkedBlockingDeque<>();
        BlockingDeque<ChatMessageResDto> messageQueue2 = new LinkedBlockingDeque<>();

        ChatMessageResDto expected1 = ChatMessageResDto.builder().messageType(MessageType.JOIN).sender(user1.getName()).content(user1.getName() + "님이 입장했습니다.").build();
        ChatMessageResDto expected2 = ChatMessageResDto.builder().messageType(MessageType.JOIN).sender(user2.getName()).content(user2.getName() + "님이 입장했습니다.").build();

        String subDestination = "/topic/room/2";

        // When
        subScribeToTopic(session1, subDestination, messageQueue1);
        subScribeToTopic(session2,subDestination,messageQueue2);

        ChatMessageResDto res1 = messageQueue1.poll(5, TimeUnit.SECONDS);
        ChatMessageResDto res2 = messageQueue2.poll(5, TimeUnit.SECONDS);
        System.out.println("res1: " + res1 + " res2: " + res2);

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



    @Test
    @Tag("webSocketSend")
    @DisplayName("그룹 메시지 전송 성공")
    void groupChatSend_suc() throws InterruptedException, ExecutionException, TimeoutException {
        // Given
        StompHeaders headers1 = new StompHeaders();
        headers1.add("Authorization", validToken1);
        StompHeaders headers2 = new StompHeaders();
        headers2.add("Authorization", validToken2);

        // WebSocket 세션 생성
        StompSession session1 = sessionConnect(url, headers1);
        StompSession session2 = sessionConnect(url, headers2);


        // 메시지 대기 큐
        BlockingDeque<ChatMessageResDto> messageQueue1 = new LinkedBlockingDeque<>();
        BlockingDeque<ChatMessageResDto> messageQueue2 = new LinkedBlockingDeque<>();

        ChatMessageResDto expected = ChatMessageResDto.builder().messageType(MessageType.GROUP_CHAT).sender(user1.getName()).content("hi").build();

        String subDestination = "/topic/chat/group/2";

        //topic 구독
        subScribeToTopic(session1, subDestination, messageQueue1);
        subScribeToTopic(session2,subDestination,messageQueue2);

        // When
        session1.send("/pub/chat/group/2", "hi");

        messageQueue2.take();
        messageQueue2.take();
        ChatMessageResDto res = messageQueue2.take();

        // Then
        assertThat(res.getSender()).isEqualTo(expected.getSender());
        assertThat(res.getContent()).isEqualTo(expected.getContent());
        assertThat(res.getMessageType()).isEqualTo(expected.getMessageType());
    }



    private StompSession sessionConnect(String url,StompHeaders header) throws ExecutionException, InterruptedException, TimeoutException {
        return stompClient.connect(url, new WebSocketHttpHeaders(), header, new StompSessionHandlerAdapter() {
        }).get(3, TimeUnit.SECONDS);
    }

    private void subScribeToTopic(StompSession session, String subDestination, BlockingQueue<ChatMessageResDto> messageQueue) throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<Void> waitForSub = new CompletableFuture<>();
        session.subscribe(subDestination, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ChatMessageResDto.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                messageQueue.offer((ChatMessageResDto) payload);
                waitForSub.complete(null); // 구독 완료 신호
            }
        });
        waitForSub.get(3, TimeUnit.SECONDS); // User 1 구독 완료까지 대기

    }

}