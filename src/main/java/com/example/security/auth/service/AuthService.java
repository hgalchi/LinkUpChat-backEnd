package com.example.security.auth.service;

import com.example.security.auth.entity.constant.TokenType;
import com.example.security.auth.entity.Refresh;
import com.example.security.user.entity.User;
import com.example.security.user.repository.RefreshRepository;
import com.example.security.common.codes.ResponseCode;
import com.example.security.common.exception.BusinessException;
import com.example.security.common.utils.JwtUtil;
import com.example.security.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
