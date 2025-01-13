package LinkUpTalk.chat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;


@Configuration
@EnableWebSocketMessageBroker
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final String ENDPOINT = "/stomp";
    private static final String TOPIC = "/topic";
    private static final String PUB = "/pub";
    private static final String QUEUE = "/queue";

    private final ChannelInterceptor jwtChannelInterceptor;
    private final StompSubProtocolErrorHandler customErrorHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(ENDPOINT)
                .setAllowedOrigins("*");
        registry.setErrorHandler(customErrorHandler);
        //.withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker(TOPIC);
        config.setUserDestinationPrefix(QUEUE);
        config.setApplicationDestinationPrefixes(PUB);
    }

    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(jwtChannelInterceptor);
    }
}
