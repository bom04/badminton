package com.example.springsecurityoauth2.oauth2.security.handler;

import com.example.springsecurityoauth2.oauth2.exception.CustomOAuth2AuthenticationException;
import com.example.springsecurityoauth2.oauth2.form.UserSaveForm;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private RequestCache requestCache=new HttpSessionRequestCache();

    private RedirectStrategy redirectStrategy=new DefaultRedirectStrategy();
    

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        // 유저가 없을때 발생하는 예외
        if (exception instanceof CustomOAuth2AuthenticationException) {
            CustomOAuth2AuthenticationException oAuth2AuthenticationException = (CustomOAuth2AuthenticationException) exception;
            
            // 여기에서 실패 처리 로직을 수행하고, 예를 들어 회원가입 페이지로 리다이렉트
            log.info("oAuthDto={}",oAuth2AuthenticationException.getDto());
            HttpSession session = request.getSession();
            session.setAttribute("oAuthDto", oAuth2AuthenticationException.getDto());

            response.sendRedirect("/signUp");
        } else {
            // 기타 실패 처리 로직
            response.sendRedirect("/login?error");
        }
    }
}
