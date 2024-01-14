package com.example.springsecurityoauth2.oauth2.controller;

import com.example.springsecurityoauth2.oauth2.domain.UserRepository;
import com.example.springsecurityoauth2.oauth2.form.FileStore;
import com.example.springsecurityoauth2.oauth2.form.OAuthDto;
import com.example.springsecurityoauth2.oauth2.form.UserSaveForm;
import com.example.springsecurityoauth2.oauth2.service.SignUpService;
import org.aspectj.lang.annotation.Before;
import org.h2.engine.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


//@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = LoginController.class)
class LoginControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private MockHttpSession session;
    @MockBean
    private SignUpService signUpService;

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private FileStore fileStore;
    @MockBean
    private ClientRegistrationRepository clientRegistrationRepository;
    @MockBean
    private OAuth2AuthorizedClientService authorizedClientService;

    private UserSaveForm form;

    @BeforeEach
    void setSession() {
        session = new MockHttpSession();
        OAuthDto oAuthDto= OAuthDto.builder()
                .email("gil1122@naver.com")
                .provider("google")
                .providerId("1fsgsfdfsf")
                .profileImage("https://test")
                .build();
        oAuthDto.generateLoginId();
        session.setAttribute("oAuthDto",oAuthDto);

        form=new UserSaveForm();
        form.setEmail("gil1122@naver.com");
        form.setNickname("test1");
        form.setIsMale(false);
        form.setBirth("1996-04-20");
        form.setCareer("D");
        form.setProfileImage("https://test");
        form.setImage(new MockMultipartFile("image", "profile.jpg", "image/jpeg", "image data".getBytes()));
    }

//    @WithMockUser(roles = "USER")
    @DisplayName("[post-signUp] 정상 동작")
    @Test
    void postSignUp1() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/signUp")
//                            .with(SecurityMockMvcRequestPostProcessors.csrf())
//                            .with(SecurityMockMvcRequestPostProcessors.oauth2Login())
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .session(session)
                            .flashAttr("user", form)  // @ModelAttribute로 사용되는 객체를 flash attribute로 설정
                )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection()) // 성공하면 redirect 하는 코드라서
                .andExpect(MockMvcResultMatchers.view().name("redirect:/"));
        // 에러가 있을 때 실행되는 코드를 확인하는 어설션도 추가 가능
    }
    @DisplayName("[post-signUp] bindingResult.hasErrors() 발생")
    @Test
    void postSignUp2() throws Exception {
        // Given
        form.setBirth(null);

        // validation 에러를 일으키는 데이터를 전송
        mockMvc.perform(MockMvcRequestBuilders.post("/signUp")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .session(session)
                                .flashAttr("user", form)  // @ModelAttribute로 사용되는 객체를 flash attribute로 설정
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("page/signUp"))
                .andExpect(MockMvcResultMatchers.model().attributeHasErrors("user"));  // bindingError

    }
}