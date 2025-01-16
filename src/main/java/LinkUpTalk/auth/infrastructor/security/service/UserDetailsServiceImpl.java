package LinkUpTalk.auth.infrastructor.security.service;

import LinkUpTalk.common.exception.BusinessException;
import LinkUpTalk.common.response.ResponseCode;
import LinkUpTalk.user.domain.User;
import LinkUpTalk.user.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new BusinessException(ResponseCode.NOT_FOUND));

        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(getAuthorities(user))
                .build();

        return userDetails;
    }

    private Collection<GrantedAuthority> getAuthorities(User user) {
        return user.getRoles().stream()
                .map(group -> group.getCode().toUpperCase())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

}
