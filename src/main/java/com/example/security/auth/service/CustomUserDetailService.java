package com.example.security.auth.service;

import com.example.security.auth.entity.Group;
import com.example.security.user.entity.User;
import com.example.security.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    /**
     * email로 UserDetails 객체를 반환
     * @param email 사용자의 이메일
     * @throws  BadCredentialsException : 이메일로 등록된 사용자가 없을 경우,비밀번호가 불일치 할 경우
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User customer = userRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("아이디혹은 비밀번호가 불일치합니다"));

        UserDetails user = org.springframework.security.core.userdetails.User.withUsername(customer.getEmail())
                .password(customer.getPassword())
                .authorities(getAuthorities(customer))
                .build();

        return user;
    }

    /**
     *사용자 권한을 반환
     * @param user 사용자 객체
     * @return Collection<GrantedAuthority> 사용자 권한 리스트
     */
    private Collection<GrantedAuthority> getAuthorities(User user) {
        Set<Group> userGroups = user.getRoles();
        Collection<GrantedAuthority> authorities = new ArrayList<>(userGroups.size());
        for (Group group : userGroups) {
            authorities.add(new SimpleGrantedAuthority(group.getCode().toUpperCase()));
        }
        return authorities;
    }

}
