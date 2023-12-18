package com.example.springsecurityoauth2.oauth2.form;

import com.example.springsecurityoauth2.oauth2.domain.UserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;

@Getter
@Builder
@ToString
public class UserDto {
    private String loginId;
    private String email;
    private String provider;
    private String providerId;

    private String profileImage;

    public void generateLoginId() {
        this.loginId = provider+'_'+providerId;
    }
}
