package LinkUpTalk.auth.presentation;

import LinkUpTalk.auth.domain.constant.TokenType;
import LinkUpTalk.auth.application.AuthService;
import LinkUpTalk.common.response.ResponseCode;
import LinkUpTalk.user.dto.UserCreateReqDto;
import LinkUpTalk.user.application.UserService;
import LinkUpTalk.common.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
     * 사용자 등록
     */
    @PostMapping("/signUp")
    public ResponseEntity<?> register(@Valid @RequestBody UserCreateReqDto req) {
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
}
