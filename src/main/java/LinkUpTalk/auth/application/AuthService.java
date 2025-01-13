package LinkUpTalk.auth.application;

import LinkUpTalk.auth.domain.constant.TokenType;
import LinkUpTalk.auth.domain.Refresh;
import LinkUpTalk.user.domain.User;
import LinkUpTalk.user.infrastructor.RefreshRepository;
import LinkUpTalk.common.response.ResponseCode;
import LinkUpTalk.common.exception.BusinessException;
import LinkUpTalk.common.util.JwtUtil;
import LinkUpTalk.user.infrastructor.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RefreshRepository refreshRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public Map<String, String> reissueToken(HttpServletRequest req) {

        String refreshToken = jwtUtil.parseRefresh(req);
        Claims claims = jwtUtil.validateToken(refreshToken);
        jwtUtil.validateClaims(claims);

        checkCategory(claims);
        checkExistRefreshToken(refreshToken);

        String email = jwtUtil.getEmail(claims);
        List<String> authorities = jwtUtil.getRoles(claims);

        Map<String, String> token = createAccessToken(email,authorities);

        Refresh newRefresh = Refresh.builder()
                .refresh(token.get(TokenType.refreshToken.name()))
                .expiration(jwtUtil.refreshExpireTime())
                .build();

        refreshRepository.deleteByRefresh(refreshToken);
        refreshRepository.save(newRefresh);

        return token;

    }

    @Transactional
    public Map<String,String> issueToken(String email,List<String> authorities) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ResponseCode.NOT_FOUND));

        Map<String, String> token = createAccessToken(email, authorities);

        //AccessToken 재발행을 위해 서버에 저장
        String expiration = jwtUtil.refreshExpireTime();
        Refresh refresh = Refresh.builder()
                .expiration(expiration)
                .refresh(token.get(TokenType.refreshToken.name()))
                .user(user)
                .build();
        refreshRepository.save(refresh);
        refreshRepository.flush();

        return token;
    }

    private void checkCategory(Claims claims) {
        if(!jwtUtil.getCategory(claims).equals(TokenType.refreshToken.name())){
            throw new BusinessException(ResponseCode.CATEGORY_NOT_REFRESH);
        }
    }

    private void checkExistRefreshToken(String refreshToken) {
        if(!refreshRepository.existsByRefresh(refreshToken)){
            throw new BusinessException(ResponseCode.TOKEN_NOT_EXIST);
        }
    }

    private Map<String,String> createAccessToken(String email,List<String> roles) {

        String newRefreshToken = jwtUtil.createToken(email, roles, TokenType.refreshToken.name());
        String newAccessToken = jwtUtil.createToken(email, roles, TokenType.accessToken.name());

        Map<String, String> token = new HashMap<>();
        token.put(TokenType.refreshToken.name(), newRefreshToken);
        token.put(TokenType.accessToken.name(), newAccessToken);
        return token;
    }


}
