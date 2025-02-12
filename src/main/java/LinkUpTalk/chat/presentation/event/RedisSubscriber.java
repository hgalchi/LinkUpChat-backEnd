package LinkUpTalk.chat.presentation.event;

import LinkUpTalk.chat.domain.constant.MessageType;
import LinkUpTalk.chat.presentation.dto.ChatMessageReqDto;
import LinkUpTalk.chat.presentation.dto.ChatMessageResDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisSubscriber implements MessageListener {

    private final SimpMessagingTemplate messageTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());
            log.info("Received message:{}", publishMessage);

            ChatMessageReqDto roomMessage = objectMapper.readValue(publishMessage, ChatMessageReqDto.class);

            if (roomMessage.getMessageType() == MessageType.DM_CHAT) {
                messageTemplate.convertAndSendToUser((String) roomMessage.getDestination(),"/queue/chat",
                        ChatMessageResDto.builder()
                                .content(roomMessage.getContent())
                                .messageType(roomMessage.getMessageType())
                                .sender(roomMessage.getSender())
                                .build());
            } else{
                messageTemplate.convertAndSend("/topic/chat/group/" + roomMessage.getDestination(),
                        ChatMessageResDto.builder()
                                .content(roomMessage.getContent())
                                .messageType(roomMessage.getMessageType())
                                .sender(roomMessage.getSender())
                                .build());
            }

        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
