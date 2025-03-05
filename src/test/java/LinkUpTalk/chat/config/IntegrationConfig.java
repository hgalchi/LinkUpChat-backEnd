package LinkUpTalk.chat.config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public abstract class IntegrationConfig {

    private static final PostgreSQLContainer POSTGRES_CONTAINER;
            static {
                POSTGRES_CONTAINER = new PostgreSQLContainer("postgres:16-alpine");
                POSTGRES_CONTAINER.start();
            }

    private static final GenericContainer REDIS_CONTAINER;
            static  {
                REDIS_CONTAINER = new GenericContainer("redis:7.0.8-alpine")
                        .withExposedPorts(6379);
                REDIS_CONTAINER.start();
            }

    private static final MongoDBContainer MONGO_CONTAINER;
            static {
                MONGO_CONTAINER = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"))
                        .withExposedPorts(27017);
                MONGO_CONTAINER.start();
            }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
    }

    @DynamicPropertySource
    static void MongoConfigureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO_CONTAINER::getReplicaSetUrl);
    }

    @DynamicPropertySource
    private static void registerRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379)
                .toString());
    }
}

