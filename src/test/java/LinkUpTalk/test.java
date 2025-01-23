package LinkUpTalk;

import LinkUpTalk.auth.domain.constant.TokenType;
import LinkUpTalk.common.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

@SpringBootTest
public class test {
    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void test() {
        String token = jwtUtil.createToken("wywudi@naver.com", Arrays.asList("ROLE_CUSTOMER"), TokenType.accessToken.name());
        System.out.println(token);
    }


}
