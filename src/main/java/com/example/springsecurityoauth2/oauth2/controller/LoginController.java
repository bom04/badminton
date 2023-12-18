package com.example.springsecurityoauth2.oauth2.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.springsecurityoauth2.oauth2.domain.UploadFile;
import com.example.springsecurityoauth2.oauth2.domain.User;
import com.example.springsecurityoauth2.oauth2.domain.UserRepository;
import com.example.springsecurityoauth2.oauth2.domain.UserRole;
import com.example.springsecurityoauth2.oauth2.form.FileStore;
import com.example.springsecurityoauth2.oauth2.form.UserDto;
import com.example.springsecurityoauth2.oauth2.form.UserSaveForm;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Controller
public class LoginController {

    private static final String authorizationRequestBaseUri = "oauth2/authorize-client";
    Map<String, String> oauth2AuthenticationUrls = new HashMap<>();

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;
    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private UserRepository userRepository;

    private FileStore fileStore;

    @Autowired
    public LoginController(FileStore fileStore) {
        this.fileStore = fileStore;
    }

    @GetMapping("/notice")
    public String noticeBoard(Model model) {
        User user1= User.builder()
                .nickname("bom04")
                .provider("kakao")
                .providerId("kakaoId")
                .role(UserRole.USER)
                .build();
        User user2= User.builder()
                .nickname("bom05")
                .provider("kakao1")
                .providerId("kakaoId1")
                .role(UserRole.USER)
                .build();
        List<User> posts=new ArrayList<>();
        posts.add(user1);
        posts.add(user2);

        model.addAttribute("posts",posts);
        return "board/notice";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        return "page/login";
    }

    @GetMapping("/test")
    public String test(Model model) {

        model.addAttribute("user",new UserSaveForm());
        return "page/test";
    }

    @GetMapping("/signUp")
    public String signUp(Model model, HttpSession session) throws Exception {
        if(session.getAttribute("userDto")==null) {
            throw new Exception("잘못된 접근입니다!");
        }
        UserDto userDto=(UserDto)session.getAttribute("userDto");
        UserSaveForm form=new UserSaveForm();
        form.setIsMale(true);
        form.setEmail(userDto.getEmail());
        form.setProfileImage(userDto.getProfileImage());
        model.addAttribute("user", form);
        log.info("get form={}",form);
        return "page/signUp";
    }

    @PostMapping("/signUp")
    public String signUp(@Validated @ModelAttribute("user") UserSaveForm form, BindingResult bindingResult,
                         HttpSession session) throws IOException {
        UserDto userDto = (UserDto) session.getAttribute("userDto");
        form.setEmail(userDto.getEmail());
        form.setProfileImage(userDto.getProfileImage());

        log.info("post form={}",form);
        log.info("post image={}",form.getImage().getOriginalFilename());

        if(bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "page/signUp";
        }

        log.info("user={}",form);
        log.info("success");

        if(userRepository.existsByNickname(form.getNickname())) {
            bindingResult.rejectValue("nickname", "nickname.duplicate", "이미 사용중인 닉네임입니다");
            log.info("Nickname already exists");
            return "page/signUp";
        }

        User user=User.builder()
                .nickname(form.getNickname())
                .email(userDto.getEmail())
                .isMale(form.getIsMale())
                .birth(form.getBirth())
                .career(form.getCareer())
                .provider(userDto.getProvider())
                .providerId(userDto.getProviderId())
                .loginId(userDto.getLoginId())
                .role(UserRole.USER)
                .build();

        // 폼으로 전달된 이미지가 소셜 프로필의 기본 이미지일때
        if(form.getImage().getOriginalFilename().equals("")) {
            user.setProfileImageName(form.getProfileImage());
        } else{
            UploadFile attachFile = fileStore.storeFile(form.getImage());
            user.setProfileImageName(attachFile.getStoreFileName());
        }

        userRepository.save(user);
        session.removeAttribute("userDto");
        return "redirect:/";
    }

    @GetMapping("/oauth_login")
    public String getLoginPage(Model model) {
        // ClientRegistration: 진행하는 oauth login이 facebook 타입인지 google 타입인지 등을 저장해놓는 변수(oauth2에 내장된 함수)
        Iterable<ClientRegistration> clientRegistrations = null;
        ResolvableType type = ResolvableType.forInstance(clientRegistrationRepository).as(Iterable.class);
        if (type != ResolvableType.NONE && ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {
            clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;
        }
        // oauth2AuthenticationUrls에 <Google,'oauth2/authorize-client/google'>형식으로 저장됨
        clientRegistrations.forEach(registration -> oauth2AuthenticationUrls.put(registration.getClientName(), authorizationRequestBaseUri + "/" + registration.getRegistrationId()));
        model.addAttribute("urls", oauth2AuthenticationUrls);

        return "oauth_login";
    }

    @GetMapping("/loginSuccess")
    public String getLoginInfo(Model model, OAuth2AuthenticationToken authentication) {
        // 로그인한 유저 가져오기
        OAuth2User oauth2User = authentication.getPrincipal();
        // 로그인한 유저 닉네임 가져오기
        if(authentication.getAuthorizedClientRegistrationId().equals("google")) {
            System.out.println("client: " + oauth2User.getAttribute("name"));
            model.addAttribute("name",oauth2User.getAttribute("name"));
        } else if(authentication.getAuthorizedClientRegistrationId().equals("kakao")) {
            System.out.println("client: " + oauth2User.getAttribute("properties"));
            model.addAttribute("name",oauth2User.getAttribute("properties"));
        }

        model.addAttribute("userList",userRepository.findAll());

        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(authentication.getAuthorizedClientRegistrationId(), authentication.getName());
        String userInfoEndpointUri = client.getClientRegistration()
            .getProviderDetails()
            .getUserInfoEndpoint()
            .getUri();

        if (!StringUtils.isEmpty(userInfoEndpointUri)) {
            // RestTemplate: http get,post 등의 HTTP 메서드를 사용하여 서버에 요청을 처리하는 클래스
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + client.getAccessToken()
                .getTokenValue());

            HttpEntity<String> entity = new HttpEntity<String>("", headers);

            ResponseEntity<Map> response = restTemplate.exchange(userInfoEndpointUri, HttpMethod.GET, entity, Map.class);
            Map userAttributes = response.getBody();
//            model.addAttribute("name", userAttributes.get("name"));
        }

        return "loginSuccess";
    }


//    @GetMapping("/loginSuccess")
    public String getLoginInfo(Model model,@AuthenticationPrincipal OAuth2User oAuth2User) {
        // 로그인한 유저 가져오기
        System.out.println("oAuth2User = " + oAuth2User.getAttribute("name"));

        return "loginSuccess";
    }
}
