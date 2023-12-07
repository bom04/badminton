package com.example.springsecurityoauth2.oauth2.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class UserSaveForm {
    @NotBlank
    private String nickname;

//    @NotBlank
//    private String gender;
//
//    @NotNull
//    @Range(min = 1930, max = 2023)
//    private Integer year;
//
//    @NotNull
//    @Range(min = 1, max = 12)
//    private Integer month;
//
//    @NotNull
//    @Range(min = 1, max = 31)
//    private Integer date;
}