package LinkUpTalk.common.util;

import LinkUpTalk.auth.domain.constant.TokenType;
import LinkUpTalk.common.response.ResponseCode;
import LinkUpTalk.common.exception.BusinessException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

//todo : 대부분 util의 네이밍이 붙은 클래스는 static method만 제공한다. 의존관계를 설정하지 않아도 호출가능한 헬퍼 패턴이 주가됨
//jwtParse로 변경 component인 만큼
@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.refresh.expiration-time}")
    private Long refreshExpirationTime;

    @Value("${jwt.access.expiration-time}")
    private Long accessExpirationTime;

    @Value("${jwt.secret-key}")
    private String key;

    private  SecretKey secretKey;

    private final String BEARER_PREFIX = "Bearer ";

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(key));
    }

    public String createToken(String email, List<String> roles, String category) {

        Long tokenExpirationTime = category.equals(TokenType.accessToken.name()) ? accessExpirationTime : refreshExpirationTime;
        Date tokenValidity = new Date(new Date().getTime() + TimeUnit.MINUTES.toMillis(tokenExpirationTime));

        return BEARER_PREFIX+Jwts.builder()
                .subject(email)
                .claim("roles", roles)
                .claim("category", category)
                .expiration(tokenValidity)
                .signWith(secretKey)
                .compact();
    }

    public Claims validateToken(String token) {
        try {
            if (token == null) {
                throw new BusinessException(ResponseCode.MALFORMED_JWT);
            }
            return parseJwtClaims(token);
        } catch (ExpiredJwtException e) {
            throw new BusinessException(ResponseCode.EXPIRED_JWT);
        } catch (SignatureException e) {
            throw new BusinessException(ResponseCode.SIGNATURE_JWT);
        } catch (Exception e) {
            throw new BusinessException(ResponseCode.MALFORMED_JWT);
        }
    }

    public boolean validateClaims(Claims claims) {
        return claims.getExpiration().after(new Date());
    }

    private Claims parseJwtClaims(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload();
    }

    public String parseRefresh(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) {
            throw new BusinessException(ResponseCode.TOKEN_NOT_EXIST);
        }

        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(TokenType.refreshToken.name()))
                .findFirst()
                .map(Cookie::getValue)
                .map(this::resolveToken)
                .orElseThrow(() -> new BusinessException(ResponseCode.TOKEN_NOT_EXIST));
    }

    public String resolveToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }throw new BusinessException(ResponseCode.TOKEN_NOT_EXIST);
    }



    public String getEmail(Claims claims) {
        return claims.getSubject();
    }

    public List<String> getRoles(Claims claims) {
        return (List<String>) claims.get("roles");
    }

    public String getCategory(Claims claims) {
        return claims.get("category").toString();
    }

    public String getRefreshExpireTime() {
        return Date.from(Instant.now().plus(Duration.ofMinutes(refreshExpirationTime))).toString();
    }


}
