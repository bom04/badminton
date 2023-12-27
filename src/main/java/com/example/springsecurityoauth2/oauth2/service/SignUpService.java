package com.example.springsecurityoauth2.oauth2.service;

import com.example.springsecurityoauth2.oauth2.domain.UploadFile;
import com.example.springsecurityoauth2.oauth2.domain.User;
import com.example.springsecurityoauth2.oauth2.domain.UserRepository;
import com.example.springsecurityoauth2.oauth2.domain.UserRole;
import com.example.springsecurityoauth2.oauth2.form.FileStore;
import com.example.springsecurityoauth2.oauth2.form.OAuthDto;
import com.example.springsecurityoauth2.oauth2.form.UserSaveForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SignUpService {
    private final UserRepository userRepository;
    private final FileStore fileStore;

    @Transactional
    public Long save(UserSaveForm form, OAuthDto oAuthDto,String profileImage) {
        User user=User.builder()
                .nickname(form.getNickname())
                .email(oAuthDto.getEmail())
                .isMale(form.getIsMale())
                .birth(form.getBirth())
                .career(form.getCareer())
                .provider(oAuthDto.getProvider())
                .providerId(oAuthDto.getProviderId())
                .profileImageName(profileImage)
                .loginId(oAuthDto.getLoginId())
                .role(UserRole.USER)
                .build();

        return userRepository.save(user).getId();
    }
    public String getProfileImageName(UserSaveForm form) throws IOException {
        String profileImage=null;
        // 폼으로 전달된 이미지가 소셜 프로필의 기본 이미지일때
        if(form.getImage().getOriginalFilename().equals("")) {
            profileImage=form.getProfileImage(); // https://~
        } else {
            UploadFile attachFile = fileStore.storeFile(form.getImage());
            profileImage=attachFile.getStoreFileName(); // ~.png
        }
        return profileImage;
    }
}
