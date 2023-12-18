package com.example.springsecurityoauth2.oauth2.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loginId; // provider+providerId 합친 값(고유의 값이므로 이 값으로 유저를 찾음)
    private String nickname;
    private String email;
    private Boolean isMale;
    private String birth;
    private String career;
    private String provider;
    private String providerId;
    private UserRole role;

    private String profileImageName; // uuid 방식의 프로필 이미지 이름

    public void setProfileImageName(String profileImageName) {
        this.profileImageName = profileImageName;
    }
}