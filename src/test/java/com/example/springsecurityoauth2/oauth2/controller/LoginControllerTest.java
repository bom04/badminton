package com.example.springsecurityoauth2.oauth2.controller;

import com.example.springsecurityoauth2.oauth2.form.OAuthDto;
import com.example.springsecurityoauth2.oauth2.form.UserSaveForm;
import com.example.springsecurityoauth2.oauth2.service.SignUpService;
import org.aspectj.lang.annotation.Before;
import org.h2.engine.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @InjectMocks
    private LoginController loginController;

    @Mock
    private SignUpService signUpService;

    private MockHttpSession session;

    @Test
    void signUp() {
    }
    @Before("")
    void setSession() {
        session = new MockHttpSession();
        OAuthDto oAuthDto= OAuthDto.builder()
                .email("gil1122@naver.com")
                .provider("kakao")
                .providerId("1fsgsfdfsf")
                .profileImage("https://test")
                .build();
        oAuthDto.generateLoginId();
        session.setAttribute("oauthDto",oAuthDto);
    }
    @Test
    void postSignUp() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
        // Given
        UserSaveForm form=UserSaveForm
        // validation 에러를 일으키는 데이터를 전송
        mockMvc.perform(MockMvcRequestBuilders.post("/signUp").session(session)
                                .param("email", "invalid-email")  // 잘못된 이메일 형식
                                .param("nickname", "")  // 닉네임이 비어있음
                        // 다른 필요한 파라미터들을 추가할 것
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("page/signUp"))
                .andExpect(MockMvcResultMatchers.model().attributeHasErrors("user"));  // "user"는 모델 속성의 이름

        // 에러가 있을 때 실행되는 코드를 확인하는 어설션도 추가 가능
    }
}