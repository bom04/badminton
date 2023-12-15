package com.example.springsecurityoauth2.oauth2.form;

import com.example.springsecurityoauth2.oauth2.validation.ValidFile;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserSaveForm {
    @NotBlank
    private String nickname;

    private String email;

    @NotNull
    private Boolean isMale;

    @NotBlank
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String birth;

    @NotBlank
    private String career; // 구력

    private String profileImage;

    @ValidFile(message = "이미지 파일은 필수입니다.")
    private MultipartFile image;
}