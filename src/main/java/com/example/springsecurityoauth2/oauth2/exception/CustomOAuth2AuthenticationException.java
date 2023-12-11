package com.example.springsecurityoauth2.oauth2.exception;

import com.example.springsecurityoauth2.oauth2.form.UserDto;
import com.example.springsecurityoauth2.oauth2.form.UserSaveForm;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class CustomOAuth2AuthenticationException extends AuthenticationException {
    private UserDto dto;

    public CustomOAuth2AuthenticationException(String msg, UserDto dto) {
        super(msg);
        this.dto=dto;
    }

}
