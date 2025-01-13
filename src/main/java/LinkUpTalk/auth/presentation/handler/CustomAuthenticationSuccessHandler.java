package LinkUpTalk.auth.presentation.handler;

import LinkUpTalk.auth.domain.constant.TokenType;
import LinkUpTalk.auth.application.AuthService;
import LinkUpTalk.common.util.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;
    private final CookieUtil cookieUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try {
            handle(response, authentication);
            clearAuthenticationAttributes(request);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * RefreshToken과 AccessToken을 header와 cookie값으로 전송
     * @param response
     * @param authentication
     * @throws IllegalAccessException
     */
    protected void handle(HttpServletResponse response, Authentication authentication) throws IllegalAccessException {

        String email = authentication.getName();
        List<String> authorities =  authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Map<String, String> token = authService.issueToken(email, authorities);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token.get(TokenType.accessToken.name()));
        response.addCookie(cookieUtil.createCookie(TokenType.refreshToken.name(), token.get(TokenType.refreshToken.name())));
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }

    //권한에 따른 redirect 페이지
   /* protected String determineTargetUrl(Authentication authentication) throws IllegalAccessException {

        Map<String, String> roleTargetUrlMap = new HashMap<String, String>();
        roleTargetUrlMap.put("ROLE_ADMIN", "/admin");
        roleTargetUrlMap.put("ROLE_CUSTOMER", "/customerHome");

        Collection<? extends GrantedAuthority> authorities =
                authentication.getAuthorities();
        for (GrantedAuthority grantedAuthority : authorities) {
            String authorityName = grantedAuthority.getAuthority();
            if (roleTargetUrlMap.containsKey(authorityName)) {
                return roleTargetUrlMap.get(authorityName);
            }
        }
        throw new IllegalAccessException();
    }*/

}
