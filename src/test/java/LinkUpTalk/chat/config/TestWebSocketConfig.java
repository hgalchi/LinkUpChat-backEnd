package LinkUpTalk.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.Mockito.mock;


public class TestWebSocketConfig {
    @Bean
    public SimpMessagingTemplate simpMessagingTemplate(){
        return mock(SimpMessagingTemplate.class);
    }
}
