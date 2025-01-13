package LinkUpTalk.common.util;

import LinkUpTalk.auth.domain.constant.TokenType;
import LinkUpTalk.common.response.ResponseCode;
import LinkUpTalk.common.exception.BusinessException;
import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {

    @Value("${jwt.refresh.expiration-time}")
    private String refreshExpirationTime;

    @Value("${jwt.access.expiration-time}")
    private String accessExpirationTime;

    @Value("${jwt.secret-key}")
    private String secret_key;

    private final String TOKEN_HADER = "Authorization";
    private final String TOKEN_PREFIX = "Bearer";


    private final JwtParser jwtParser;

    public JwtUtil() {
        this.jwtParser = Jwts.parser().setSigningKey("mysecretkey");
    }

    /**
     * 토큰 생성
     * @param email 사용자 이메일
     * @param roles 사용자 권한
     * @param category  accesstoken,refreshtoken을 구분하기 위한 문자열
     * @return 생성된 JWT토큰
     */
    public String createToken(String email, List<String> roles, String category) {

        Claims claims = Jwts.claims().setSubject(email);
        claims.put("roles", roles);
        claims.put("category", category);

        Long token = (Objects.equals(category, TokenType.accessToken.name()) ? Long.parseLong(accessExpirationTime) : Long.parseLong(refreshExpirationTime));
        //new Date(System.currentTimeMillis()+accessEcpiration
        Date tokenValidity = new Date(new Date().getTime() + TimeUnit.MINUTES.toMillis(token));

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(tokenValidity)
                .signWith(SignatureAlgorithm.HS256, secret_key)
                .compact();
    }

    /**
     * refresh만료 기간 반환
     */
    public String refreshExpireTime() {
        Date tokenCreateTime = new Date();
        Date tokenValidity = new Date(tokenCreateTime.getTime() + TimeUnit.MINUTES.toMillis(Long.parseLong(refreshExpirationTime)));
        return tokenValidity.toString();
    }


    /**
     * JWT 토큰을  Claims으로 변환
     * @return 토큰에서 추출한 Claims
     */
    private Claims parseJwtClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    /**
     * token을 cliams로 변환
     * @param token
     * @return
     */
    public Claims tokenReverseClaims(String token) throws Exception {
        Claims claims = validateToken(token);
        if(validateClaims(claims)) return claims;
        return null;
    }

    /**
     * JWT 토큰의 유효성 검증
     * @throws  SignatureException,MalformedJwtException,ExpiredJwtException 토큰이 유효하지 않을 때 발생
     * @param token
     * @return
     */
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




    /**
     * Claims의 만료 기간 검증
     * @return 만료기간이 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateClaims(Claims claims) {
        return claims.getExpiration().after(new Date());
    }

    /**
     * Http 요청쿠키에서 refresh 토큰 추출
     */
    public String parseRefresh(HttpServletRequest req) {
        //get refresh token
        String refresh = null;
        Cookie[] cookies = req.getCookies();
        if (cookies == null) {
            throw new BusinessException(ResponseCode.TOKEN_NOT_EXIST);
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(TokenType.refreshToken.name())) {
                refresh = cookie.getValue();
            }
        }
        return refresh;

    }

    /**
     * Bearer 토큰에서 JWT 토큰 분리
     */
    public String resolveToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }return null;
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


}
