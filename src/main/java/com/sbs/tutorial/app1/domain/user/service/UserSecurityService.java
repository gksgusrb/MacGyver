package com.sbs.tutorial.app1.domain.user.service;

import com.sbs.tutorial.app1.domain.user.User;
import com.sbs.tutorial.app1.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserSecurityService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을수 없습니다"));

        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_YOU")
        );
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                "",
                authorities
        );
    }
}
