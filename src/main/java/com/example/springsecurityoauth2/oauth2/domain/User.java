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
@Table(name = "Member")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loginId; // provider+providerId 합친 값(고유의 값이므로 이 값으로 유저를 찾음)
    private String nickname;
    private String email;
    private Boolean isMale;
    private String birth;
    private String provider;
    private String providerId;

    @Enumerated(EnumType.STRING)
    private Career career;
    @Enumerated(EnumType.STRING) // enum type을 기본 int가 아니라 string으로 저장해서 무슨 코드를 의미하는지 바로 찾겠다
    private UserRole role;

    private String profileImageName; // uuid 방식의 프로필 이미지 이름

    public void setProfileImageName(String profileImageName) {
        this.profileImageName = profileImageName;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setIsMale(Boolean male) {
        isMale = male;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public void setCareer(Career career) {
        this.career = career;
    }
}