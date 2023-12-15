package com.example.springsecurityoauth2.oauth2.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

public class ValidFileValidator implements ConstraintValidator<ValidFile, MultipartFile> {
// 파일 크기 넘어가면 예외 발생시키기
//    private static final String ERROR_MESSAGE = "File too Large.";
//    @Value("${spring.servlet.multipart.max-file-size}")
//    private long FILE_SIZE;

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        return file != null && !file.isEmpty();
    }
}
