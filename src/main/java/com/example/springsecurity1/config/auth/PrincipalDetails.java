package com.example.springsecurity1.config.auth;

import com.example.springsecurity1.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

/*
시큐리티가 /login을 인터셉트 해서 로그인을 진행
완료되면 시큐리티 session을 만든다(SecurityContextHolder)
그 session에는 Authentication 타입 객체만 들어갈 수 있다.
Authentication 안에는 User 정보가 있어야 한다.
User 객체 타입은 UserDetails 타입 객체이다.

Security Session -> Authentication -> UserDetails(여기선 PrincipalDetails) 구조
*/

public class PrincipalDetails implements UserDetails {

    private User user;

    public PrincipalDetails(User user) {
        this.user = user;
    }

    // 권한 리턴
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });
        return collect;
    }


    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // 계정 만료 안되었는지?
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정 잠금 안되었는지?
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 계정 비밀번호 만료 안되었는지?
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 사용 가능한지?
    @Override
    public boolean isEnabled() {
        return true;
    }
}
