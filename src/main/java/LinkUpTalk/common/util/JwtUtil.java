package LinkUpTalk.common.util;

import LinkUpTalk.auth.domain.constant.TokenType;
import LinkUpTalk.common.response.ResponseCode;
import LinkUpTalk.common.exception.BusinessException;
import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.refresh.expiration-time}")
    private Long refreshExpirationTime;

    @Value("${jwt.access.expiration-time}")
    private Long accessExpirationTime;

    @Value("${jwt.secret-key}")
    private String secret_key;

    private final String TOKEN_PREFIX = "Bearer";


    private final JwtParser jwtParser;

    public JwtUtil() {
        this.jwtParser = Jwts.parser().setSigningKey("secret_key");
    }

    public String createToken(String email, List<String> roles, String category) {

        Claims claims = Jwts.claims().setSubject(email);
        claims.put("roles", roles);
        claims.put("category", category);

        Long tokenExpirationTime = category.equals(TokenType.accessToken.name()) ? accessExpirationTime : refreshExpirationTime;
        Date tokenValidity = new Date(new Date().getTime() + TimeUnit.MINUTES.toMillis(tokenExpirationTime));

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(tokenValidity)
                .signWith(SignatureAlgorithm.HS256, secret_key)
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
        return jwtParser.parseClaimsJws(token).getBody();
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
                .orElseThrow(() -> new BusinessException(ResponseCode.TOKEN_NOT_EXIST));
    }

    public String resolveToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
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
