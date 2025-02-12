package LinkUpTalk.chat.presentation.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class RedisPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CHANNEL_TOPIC = "chatroom";

    public <T> void sendMessage(T message){
        try {
            redisTemplate.convertAndSend(CHANNEL_TOPIC, message);
        } catch (Exception e) {
            log.error("{} 채널 메시지 발행 실패", CHANNEL_TOPIC);
        }
    }

}
