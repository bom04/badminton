package com.example.springsecurityoauth2.oauth2.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.Authentication;

@Getter
@Builder
@ToString
public class SessionDto {
    private String loginId; // loginId로 유저 찾기
    private String nickname;
    private String profileImage;
    private Authentication authentication;
}
