package com.example.springsecurityoauth2.oauth2.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

// anonymous 사용자가 접근할때 오류 발생
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private String errorPage;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        String deniedUrl=errorPage+"?exception="+authException.getMessage();
        response.sendRedirect(deniedUrl);
    }
    public void setErrorPage(String s) {
        this.errorPage=s;
    }
}