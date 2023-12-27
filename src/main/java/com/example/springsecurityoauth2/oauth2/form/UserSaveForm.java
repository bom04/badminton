package com.example.springsecurityoauth2.oauth2.form;

import com.example.springsecurityoauth2.oauth2.validation.ValidFile;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserSaveForm {
    @NotBlank
    @Size(min=2,max=7)
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

//    @ValidFile(message = "이미지 파일은 필수입니다.")
    private MultipartFile image;
}