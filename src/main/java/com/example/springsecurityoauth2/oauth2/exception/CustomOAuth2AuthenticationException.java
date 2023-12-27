package com.example.springsecurityoauth2.oauth2.exception;

import com.example.springsecurityoauth2.oauth2.form.OAuthDto;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class CustomOAuth2AuthenticationException extends AuthenticationException {
    private OAuthDto dto;

    public CustomOAuth2AuthenticationException(String msg, OAuthDto dto) {
        super(msg);
        this.dto=dto;
    }

}
