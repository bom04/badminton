package com.example.springsecurityoauth2.oauth2.form;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class OAuthDto {
    private String loginId;

    private String email;
    private String provider;
    private String providerId;

    private String profileImage;

    public void generateLoginId() {
        this.loginId = provider+'_'+providerId;
    }
}
