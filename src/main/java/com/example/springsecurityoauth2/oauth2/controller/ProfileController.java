package com.example.springsecurityoauth2.oauth2.controller;

import com.example.springsecurityoauth2.oauth2.domain.User;
import com.example.springsecurityoauth2.oauth2.domain.UserRole;
import com.example.springsecurityoauth2.oauth2.form.UserSaveForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {
    private User user;
    private void setUser() {
        user=User.builder()
                .nickname("a11")
                .provider("google")
                .providerId("13hjf83")
                .loginId("google_13hjf83")
                .email("gmaru6420@gmail.com")
                .isMale(false)
                .role(UserRole.USER)
                .birth("1996-03-12")
                .career("B")
                .profileImageName("https://lh3.googleusercontent.com/a/ACg8ocLqNV1r7xY49rNX5lOoLYDg7ChGAI6QW_Z2FUrcSJpyzw=s96-c")
                .build();
    }
    @GetMapping("/profile")
    public String profile(Model model) {
        setUser();
        model.addAttribute("user",user);
        return "user/profile";
    }
}
