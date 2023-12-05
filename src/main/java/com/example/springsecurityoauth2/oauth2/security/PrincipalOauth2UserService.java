package com.example.springsecurityoauth2.oauth2.security;

import com.example.springsecurityoauth2.oauth2.domain.User;
import com.example.springsecurityoauth2.oauth2.domain.UserRepository;
import com.example.springsecurityoauth2.oauth2.domain.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

// 구글 로그인만 실행됨
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//        // 로그인한 유저 정보
//        OAuth2User oAuth2User = super.loadUser(userRequest);
//        System.out.println("oAuth2User.getAttributes(): "+oAuth2User.getAttributes());
//
//        String provider = userRequest.getClientRegistration().getRegistrationId();
//        String providerId = oAuth2User.getAttribute("sub"); // 구글인지 카카오인지 등
//        String loginId = provider + "_" +providerId;
//
//        Optional<User> optionalUser = userRepository.findByLoginId(loginId);
//        User user;
//
//        if(optionalUser.isEmpty()) {
//            user = User.builder()
//                    .loginId(loginId)
//                    .nickname(oAuth2User.getAttribute("name"))
//                    .provider(provider)
//                    .providerId(providerId)
//                    .role(UserRole.USER)
//                    .build();
//            userRepository.save(user);
//        } else {
//            user = optionalUser.get();
//        }
//
//        return new PrincipalDetails(user, oAuth2User.getAttributes());
//    }

    // 구글, 카카오 로그인 포함
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        OAuth2UserInfo oAuth2UserInfo = null;

        String provider = userRequest.getClientRegistration().getRegistrationId();

        if(provider.equals("google")) {
            log.info("구글 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else if(provider.equals("kakao")) {
            log.info("카카오 로그인 요청");
            oAuth2UserInfo = new KakaoUserInfo((Map)oAuth2User.getAttributes());
        }

        String providerId = oAuth2UserInfo.getProviderId();
        String email = oAuth2UserInfo.getEmail();
        String loginId = provider + "_" + providerId;
        String nickname = oAuth2UserInfo.getName();


        Optional<User> optionalUser = userRepository.findByLoginId(loginId);
        User user = null;

        if(optionalUser.isEmpty()) {
            user = User.builder()
                    .loginId(loginId)
                    .nickname(nickname)
                    .provider(provider)
                    .providerId(providerId)
                    .role(UserRole.USER)
                    .build();
            userRepository.save(user);
        } else {
            user = optionalUser.get();
        }

        return new PrincipalDetails(user, oAuth2User.getAttributes());
    }
}