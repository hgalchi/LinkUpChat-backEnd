package LinkUpTalk.auth.infrastructor.security.filter;

import LinkUpTalk.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final String TOKEN_HEADER = "Authorization";
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            String bearerToken =request.getHeader(TOKEN_HEADER);
            String accessToken = jwtUtil.resolveToken(bearerToken);
            Claims claims = jwtUtil.validateToken(accessToken);

            if (jwtUtil.validateClaims(claims)) {
                List<SimpleGrantedAuthority> authorities = jwtUtil.getRoles(claims).stream()
                        .map(t -> new SimpleGrantedAuthority(t.toUpperCase()))
                        .collect(Collectors.toList());
                String email = jwtUtil.getEmail(claims);
                Authentication authentication = new UsernamePasswordAuthenticationToken(email, "", authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            request.setAttribute("exception", e);
        }
        filterChain.doFilter(request, response);
    }
}
