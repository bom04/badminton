package com.example.springsecurityoauth2.oauth2.handler;

import com.example.springsecurityoauth2.oauth2.dto.PrincipalUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Slf4j
@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private RequestCache requestCache=new HttpSessionRequestCache();

    private RedirectStrategy redirectStrategy=new DefaultRedirectStrategy();

    // db에 user 저장한 후 실행되는 메소드
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        PrincipalUserDetails userDetails = (PrincipalUserDetails) authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

        // 세션에 유저 정보 저장
        SecurityContextHolder.getContext().setAuthentication(
                new OAuth2AuthenticationToken(
                        userDetails,
                        authorities,
                        oauthToken.getAuthorizedClientRegistrationId()
                )
        );

        // 바로 이전에 요청한 url로 이동하도록
        setDefaultTargetUrl("/");
        SavedRequest savedRequest=requestCache.getRequest(request,response);
        if(savedRequest!=null) {
            String targetUrl = savedRequest.getRedirectUrl();
            redirectStrategy.sendRedirect(request,response,targetUrl);
        } else {
            redirectStrategy.sendRedirect(request,response,getDefaultTargetUrl());
        }
    }
}
