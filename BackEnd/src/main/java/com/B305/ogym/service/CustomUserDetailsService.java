package com.B305.ogym.service;

import com.B305.ogym.domain.users.UserRepository;
import com.B305.ogym.domain.users.common.UserBase;
import com.B305.ogym.exception.user.UserNotFoundException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("userDetailsService")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // 해당 이메일이 존재하는지 확인
    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String email) {
        UserBase result;
        result = userRepository.findOneWithAuthoritiesByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("해당하는 이메일이 존재하지 않습니다."));

        return createUser(email, result);
    }

    // 해당 이메일로 인가된 객체 생성
    private org.springframework.security.core.userdetails.User createUser(String username,
        UserBase userBase) {

        List<GrantedAuthority> grantedAuthority = new ArrayList<>();
        grantedAuthority
            .add(new SimpleGrantedAuthority(userBase.getAuthority()));
        return new org.springframework.security.core.userdetails.User(userBase.getEmail(),
            userBase.getPassword(),
            grantedAuthority);
    }
}
