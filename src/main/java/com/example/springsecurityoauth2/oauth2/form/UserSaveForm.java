package com.example.springsecurityoauth2.oauth2.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

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

    @NotBlank
    private String profileImage;
}