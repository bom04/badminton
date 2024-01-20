package com.example.springsecurityoauth2.oauth2.controller;

import com.example.springsecurityoauth2.oauth2.domain.Career;
import com.example.springsecurityoauth2.oauth2.domain.User;
import com.example.springsecurityoauth2.oauth2.domain.UserRepository;
import com.example.springsecurityoauth2.oauth2.dto.PrincipalUserDetails;
import com.example.springsecurityoauth2.oauth2.form.UserSaveForm;
import com.example.springsecurityoauth2.oauth2.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;

@Slf4j
@Controller
public class ProfileController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

//    private void setUser() {
//        user=User.builder()
//                .nickname("a11")
//                .provider("google")
//                .providerId("13hjf83")
//                .loginId("google_13hjf83")
//                .email("gmaru6420@gmail.com")
//                .isMale(false)
//                .role(UserRole.USER)
//                .birth("1996-03-12")
//                .career("D")
//                .profileImageName("https://lh3.googleusercontent.com/a/ACg8ocLqNV1r7xY49rNX5lOoLYDg7ChGAI6QW_Z2FUrcSJpyzw=s96-c")
//                .build();
//    }
    @GetMapping("/profile")
    public String profile(Model model) {
        PrincipalUserDetails userDetails=(PrincipalUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user=userDetails.getUser();
        model.addAttribute("user",user);
        return "user/profile";
    }
    @GetMapping("/profile/edit")
    public String profileEdit(Model model) {
        PrincipalUserDetails userDetails=(PrincipalUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user=userDetails.getUser();
        UserSaveForm form=new UserSaveForm();
        form.setEmail(user.getEmail());
        form.setIsMale(user.getIsMale());
        form.setNickname(user.getNickname());
        form.setBirth(user.getBirth());
        form.setCareer(user.getCareer().name());
        form.setProfileImage(user.getProfileImageName());
        model.addAttribute("user",form);
        return "user/profileEdit";
    }
    @PostMapping("/profile/edit")
    public String profileEdit(@Validated @ModelAttribute("user") UserSaveForm form, BindingResult bindingResult,
                         HttpSession session) throws IOException {
        PrincipalUserDetails principalUserDetails=(PrincipalUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user=principalUserDetails.getUser();
        form.setEmail(user.getEmail());
        form.setProfileImage(user.getProfileImageName());

        log.info("post form={}",form);
        log.info("post image={}",form.getImage().getOriginalFilename());

        if(bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "user/profileEdit";
        }

        log.info("user={}",form);
        log.info("success");

        if(userRepository.existsByNickname(form.getNickname())) {
            bindingResult.rejectValue("nickname", "nickname.duplicate", "이미 사용중인 닉네임입니다");
            log.info("Nickname already exists");
            return "user/profileEdit";
        }

        // 계속 같은 이미지를 사용하면 기존에 이미지가 존재하므로 더이상 새로운 이미지가 생성되지 않음
        String profileImage = userService.getProfileImageName(form);

        Long userId = userService.update(form, user, profileImage);
        User user3=userRepository.findById(userId).get();
        updateSession(user3);
        return "redirect:/profile";
    }
    public void updateSession(User user) {
        // 현재 SecurityContext에서 Authentication 객체 가져오기
        Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) currentAuthentication;
        PrincipalUserDetails userDetails=(PrincipalUserDetails)currentAuthentication.getPrincipal();
        userDetails.setUser(user);
        // 새로운 Authentication 객체 생성
        Authentication newAuthentication = new OAuth2AuthenticationToken(
                userDetails,
                currentAuthentication.getAuthorities(),
                oauthToken.getAuthorizedClientRegistrationId()
        );

        // SecurityContextHolder에 새로운 Authentication 설정
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
    }
}
