package LinkUpTalk.chat.config;

import LinkUpTalk.common.exception.BusinessException;
import LinkUpTalk.common.response.ResponseCode;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
//import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;
import redis.embedded.RedisServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Configuration
@Profile("test")
public class EmbeddedRedisConfig {

    @Value("${spring.redis.port}")
    public int port;

    private RedisServer redisServer;

    @PostConstruct
    public void postConstruct() throws IOException {
        int redisPort = isRedisRunning() ? findAvailablePort() : port;
        redisServer = new RedisServer(redisPort);
        redisServer.start();
    }

    @PreDestroy
    public void preDestroy() throws IOException {
        redisServer.stop();
    } public int findAvailablePort() throws IOException {
        for (int port = 10000; port <= 65535; port++) {
            Process process = executeGrepProcessCommand(port);
            if (!isRunning(process)) {
                return port;
            }
        }

        throw new BusinessException(ResponseCode.SYSTEM_ERROR);
    }

    /**
     * Embedded Redis가 현재 실행중인지 확인
     */
    private boolean isRedisRunning() throws IOException {
        return isRunning(executeGrepProcessCommand(port));
    }

    /**
     * 해당 port를 사용중인 프로세스를 확인하는 sh 실행
     */
    private Process executeGrepProcessCommand(int redisPort) throws IOException {
        String command = String.format("netstat -nat | grep LISTEN|grep %d", redisPort);
        String[] shell = {"/bin/sh", "-c", command};

        return Runtime.getRuntime().exec(shell);
    }

    /**
     * 해당 Process가 현재 실행중인지 확인
     */
    private boolean isRunning(Process process) {
        String line;
        StringBuilder pidInfo = new StringBuilder();

        try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            while ((line = input.readLine()) != null) {
                pidInfo.append(line);
            }
        } catch (Exception e) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR);
        }

        return StringUtils.hasText(pidInfo.toString());
    }
}

