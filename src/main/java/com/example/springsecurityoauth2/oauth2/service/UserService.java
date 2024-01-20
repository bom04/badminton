package com.example.springsecurityoauth2.oauth2.service;

import com.example.springsecurityoauth2.oauth2.domain.*;
import com.example.springsecurityoauth2.oauth2.form.FileStore;
import com.example.springsecurityoauth2.oauth2.form.OAuthDto;
import com.example.springsecurityoauth2.oauth2.form.UserSaveForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final FileStore fileStore;

    @Transactional
    public Long save(UserSaveForm form, OAuthDto oAuthDto,String profileImage) {
        Career career = convertCareerStringToEnum(form.getCareer());

        User user=User.builder()
                .nickname(form.getNickname())
                .email(oAuthDto.getEmail())
                .isMale(form.getIsMale())
                .birth(form.getBirth())
                .career(career)
                .provider(oAuthDto.getProvider())
                .providerId(oAuthDto.getProviderId())
                .profileImageName(profileImage)
                .loginId(oAuthDto.getLoginId())
                .role(UserRole.USER)
                .build();

        return userRepository.save(user).getId();
    }
    @Transactional
    public Long update(UserSaveForm form,User user,String profileImage) {
        User user1 = userRepository.findById(user.getId()).get();
        Career career = convertCareerStringToEnum(form.getCareer());
        user1.setNickname(form.getNickname());
        user1.setBirth(form.getBirth());
        user1.setIsMale(form.getIsMale());
        user1.setCareer(career);
        user1.setProfileImageName(profileImage);
        return userRepository.save(user1).getId();
    }
    public String getProfileImageName(UserSaveForm form) throws IOException {
        String profileImage=null;
        // 폼으로 전달된 이미지가 소셜 프로필의 기본 이미지일때
        if(form.getImage().getOriginalFilename().equals("")) {
            profileImage=form.getProfileImage(); // https://~
        } else { // 직접 추가한 이미지일때
            UploadFile attachFile = fileStore.storeFile(form.getImage());
            profileImage=attachFile.getStoreFileName(); // ~.png
        }
        return profileImage;
    }

    public Career convertCareerStringToEnum(String careerString) {
        return Career.valueOf(careerString);
    }
}
