package LinkUpTalk.chat.infrastructor.webSocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

@Configuration
@EnableWebSocketSecurity
public class WebSocketSecurityConfig {

    @Bean
    AuthorizationManager<Message<?>> messageAuthorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        messages
                .simpSubscribeDestMatchers("/user/queue/errors").permitAll()
                .simpSubscribeDestMatchers("/topic/room","/user/queue").hasRole("CUSTOMER")
                .simpTypeMatchers(SimpMessageType.MESSAGE).denyAll()
                .anyMessage().denyAll();
        return messages.build();
    }


}
