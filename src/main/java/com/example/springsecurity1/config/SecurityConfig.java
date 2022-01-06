package com.example.springsecurity1.config;

import com.example.springsecurity1.config.auth.PrincipalDetailsService;
import com.example.springsecurity1.config.oauth.PrincipalOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
// Secured 애노테이션(메소드 위에 쓰며 입력한 ROLE 만 접속 가능) 활성화,
// PreAuthorize, PostAuthorize 애노테이션 활성화(Pre는 메서드 시각 전에 작동, Post는 후에; @Secured 랑 비슷한데 hasRole()사용해서 여러 ROLE 허용),
// 글로벌로 작성을 추천함, 간단하게 사용할 때만 사용하기
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;

    // 메서드가 반환하는 겍체가 빈 컨테이너에 등록됨
    @Bean
    public BCryptPasswordEncoder encodePwd() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/user/**").authenticated()
                .antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll()
                .and()
                .formLogin()
                .loginPage("/loginForm")
                .loginProcessingUrl("/login")  // /login으로 이동 시 로그인 스프링 시큐리티에서 진행
                .defaultSuccessUrl("/")
                .and()
                .oauth2Login()
                .loginPage("/loginForm") // 구글 로그인 완료; 후처리 필요함
                .userInfoEndpoint()
                .userService(principalOauth2UserService);
    }
}
