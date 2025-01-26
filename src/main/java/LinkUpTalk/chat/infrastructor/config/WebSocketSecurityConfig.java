package LinkUpTalk.chat.infrastructor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSocketSecurity
public class WebSocketSecurityConfig {

    @Bean
    AuthorizationManager<Message<?>> messageAuthorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        messages
                .nullDestMatcher().permitAll()//CONNECT,HEARTBEAT 등은 permitAll()로 열어둔다.
                .simpSubscribeDestMatchers("/topic/room/**", "/user/queue/**").authenticated()
                .simpDestMatchers("/pub/**").authenticated() //사용자가 웹을 통해 브로드캐스팅
                .simpTypeMatchers(SimpMessageType.MESSAGE).denyAll()// 사용자가 브로드캐스팅을 못하게함.
                .anyMessage().denyAll();
        return messages.build();
    }

    @Bean("csrfChannelInterceptor")
    public ChannelInterceptor csrfChannelInterceptor(){
        return new ChannelInterceptor() {
        };
    }
}

