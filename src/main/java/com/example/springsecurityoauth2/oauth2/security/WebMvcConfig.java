package com.example.springsecurityoauth2.oauth2.security;

import com.example.springsecurityoauth2.oauth2.interceptor.UserInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInterceptor())
                .addPathPatterns("/**"); // 모든 경로에 적용
    }

    @Bean
    public UserInterceptor userInterceptor() {
        return new UserInterceptor();
    }
}