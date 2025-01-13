package LinkUpTalk.auth.application.security;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {


    private final UserDetailsService userDetailService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        System.out.println("custome provider");

        final String username = (authentication.getPrincipal() == null) ? "NONE_PROVIDED" : authentication.getPrincipal().toString();
        if (StringUtils.isEmpty(username)) {
            throw new BadCredentialsException("Invalid login details");
        }
        UserDetails user = null;
        try {
            user = userDetailService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            throw new BadCredentialsException("Invalid login details");
        }
        return createSuccAuthentication(authentication, user);
    }

    private Authentication createSuccAuthentication(Authentication authentication, UserDetails user) {

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities());
        token.setDetails(authentication.getDetails());
        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
