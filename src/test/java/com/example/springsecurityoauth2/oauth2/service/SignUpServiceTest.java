package com.example.springsecurityoauth2.oauth2.service;

import com.example.springsecurityoauth2.oauth2.domain.UploadFile;
import com.example.springsecurityoauth2.oauth2.domain.User;
import com.example.springsecurityoauth2.oauth2.domain.UserRepository;
import com.example.springsecurityoauth2.oauth2.form.FileStore;
import com.example.springsecurityoauth2.oauth2.form.OAuthDto;
import com.example.springsecurityoauth2.oauth2.form.UserSaveForm;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import static org.assertj.core.api.BDDAssumptions.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignUpServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private FileStore fileStore;
    @InjectMocks
    private SignUpService signUpService;

    @Test
    void save() {
        // given
        UserSaveForm form = new UserSaveForm();
        form.setNickname("testNickname");
        form.setIsMale(true);
        form.setBirth("1990-01-01");
        form.setCareer("B");

        OAuthDto oAuthDto = OAuthDto.builder()
                .email("test@example.com")
                .provider("google")
                .providerId("11")
                .loginId("testLoginId")
                .build();

        String profileImage = "test.png";

        // mocking
        User mockUser = mock(User.class);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(mockUser.getId()).thenReturn(1L);

        // when
        Long userId = signUpService.save(form, oAuthDto, profileImage);
        // then
        verify(userRepository, times(1)).save(any(User.class));
        assertEquals(1L, userId);
    }

    @Test
    void getProfileImageName() throws IOException {
        // if-소셜 기본 이미지일때
        UserSaveForm form=new UserSaveForm();
        form.setImage(new MockMultipartFile("image","","image/jpeg","image data".getBytes()));
        form.setProfileImage("https://mock-filename");

        //when
        String profileImage=signUpService.getProfileImageName(form);
        //then
        assertEquals("https://mock-filename", profileImage);
        verify(fileStore, times(0)).storeFile(any(MultipartFile.class));


        // else-소셜 기본 이미지가 아닐때
        //given
        form.setImage(new MockMultipartFile("image", "mock-filename.jpg", "image/jpeg", "image data".getBytes()));

        //mocking
        UploadFile mockUploadFile = new UploadFile("mock-filename.jpg", "1ff.jpg");
        when(fileStore.storeFile(any(MultipartFile.class))).thenReturn(mockUploadFile);

        //when
        profileImage=signUpService.getProfileImageName(form);
        //then
        assertEquals("1ff.jpg", profileImage);
        verify(fileStore, times(1)).storeFile(any(MultipartFile.class));
    }
}