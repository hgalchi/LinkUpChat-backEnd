package com.example.security.auth.controller;

import com.example.security.auth.entity.constant.TokenType;
import com.example.security.auth.service.AuthService;
import com.example.security.common.codes.ResponseCode;
import com.example.security.common.dto.UserCreateReqDto;
import com.example.security.user.service.UserService;
import com.example.security.common.utils.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final CookieUtil cookieUtil;

    /**
     * 사용자 등록 [회원가입]
     * @param req
     * @return
     */
    @PostMapping("/signUp")
    public ResponseEntity<?> register(@Validated @RequestBody UserCreateReqDto req) {
        userService.create(req);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    /**
     * 사용자 탈퇴
     */
    @PreAuthorize("hasRole('ADMIN') or @resourceAuthService.isUserOwner(authentication, #userId)")
    @DeleteMapping("/signOut")
    public ResponseEntity<ResponseCode> delete(@PathVariable Long userId) {
        userService.delete(userId);
        return ResponseEntity.ok(ResponseCode.STATE_SUCC);
    }

    /**
     * 토큰 재발급
     */
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest req,HttpServletResponse response) {
        Map<String,String> token=authService.reissueToken(req);

        Cookie cookie = cookieUtil.createCookie(TokenType.refreshToken.name(), token.get(TokenType.refreshToken.name()));
        response.addCookie(cookie);
        HttpHeaders header = new HttpHeaders();
        header.add(TokenType.accessToken.name(), token.get(TokenType.accessToken.name()));

        return ResponseEntity.ok()
                .headers(header)
                .build();
    }

    /**
     * 로그인 오류
     * @param error
     * @param exceptionMessage
     * @return
     */
    @GetMapping("/login")
    public String loginError(@RequestParam(value = "error", required = false) String error,
                             @RequestParam(value = "exceptionMessage", required = false) String exceptionMessage) {
        return "error: " + error + ", exception: " + exceptionMessage;
    }

    /**
     * 접근 권한 오류
     */
    @GetMapping("/customError")
    public String customError() {

        return "404 페이지 접근 권한이 없습니다.";
    }
}
