package LinkUpTalk.chat.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
//import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

import java.io.IOException;

@Configuration
@Profile("test")
public class EmbeddedRedisConfig {

    @Value("${spring.redis.port}")
    public int port;

    private RedisServer redisServer;

    @PostConstruct
    public void postConstruct() throws IOException {
        redisServer = new RedisServer(port);
        redisServer.start();
    }

    @PreDestroy
    public void preDestroy() throws IOException {
        redisServer.stop();
    }
}
