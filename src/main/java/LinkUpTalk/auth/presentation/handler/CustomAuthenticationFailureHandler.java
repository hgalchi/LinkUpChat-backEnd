package LinkUpTalk.auth.presentation.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;

@Component
@Log4j2
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {

        String errorMessage = "";

        if (exception instanceof BadCredentialsException) {
            errorMessage = "아이디혹은 비밀번호가 불일치합니다";
        }else {
            errorMessage = "알 수 없는 이유로 로그인이 불가합니다.";
        }
        log.info("failureHandler :" + exception);

        errorMessage=URLEncoder.encode(errorMessage,"UTF-8");
        setDefaultFailureUrl("/login?error=true&exceptionMessage="+errorMessage);
        super.onAuthenticationFailure(request,response,exception);

    }
}
