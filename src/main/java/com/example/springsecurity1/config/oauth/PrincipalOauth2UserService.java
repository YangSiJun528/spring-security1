package com.example.springsecurity1.config.oauth;

import com.example.springsecurity1.config.auth.PrincipalDetails;
import com.example.springsecurity1.config.auth.provider.FacebookUserInfo;
import com.example.springsecurity1.config.auth.provider.GoogleUserInfo;
import com.example.springsecurity1.config.auth.provider.NaverUserInfo;
import com.example.springsecurity1.config.auth.provider.Oauth2UserInfo;
import com.example.springsecurity1.model.User;
import com.example.springsecurity1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    @Lazy
    @Autowired
    public BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserRepository userRepository;

    // 구글에서 받은 userRequest 데이터를 후처리
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        Oauth2UserInfo oauth2UserInfo = null;

        // code를 통해 구성한 정보
        System.out.println("userRequest clientRegistration : " + userRequest.getClientRegistration());
        // token을 통해 응답받은 회원정보
        System.out.println("oAuth2User : " + oAuth2User);

        switch (userRequest.getClientRegistration().getRegistrationId()) {
            case "google":
                oauth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
                break;
            case "facebook":
                oauth2UserInfo = new FacebookUserInfo(oAuth2User.getAttributes());
                break;
            case "naver":
                oauth2UserInfo = new NaverUserInfo((Map)oAuth2User.getAttributes().get("response"));
                break;
            default:
                System.out.println("잘못된 Oauth 요청입니다.");
        }

        String provider = oauth2UserInfo.getProvider(); // google
        String providerId = oauth2UserInfo.getProviderId();
        String username = provider+"_"+providerId; // ex) google_112312312312321321
        String password = bCryptPasswordEncoder.encode("구글비번");
        String email = oauth2UserInfo.getEmail();
        String role = "ROLE_USER";

        User userEntity = userRepository.findByUsername(username);

        if(userEntity == null) {
            userEntity = User.builder()
                    .username(username)
                    .email(email)
                    .password(password)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(userEntity);
        }

        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
    }
}
