package com.example.springsecurityoauth2.oauth2.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.example.springsecurityoauth2.oauth2.handler.CustomAccessDeniedHandler;
import com.example.springsecurityoauth2.oauth2.handler.CustomAuthenticationEntryPoint;
import com.example.springsecurityoauth2.oauth2.interceptor.UserInterceptor;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
@PropertySource("application-oauth2.properties")
@RequiredArgsConstructor
public class SecurityConfig {

    private final PrincipalOauth2UserService principalOauth2UserService;

    @Autowired
    private AuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    private AuthenticationFailureHandler customAuthenticationFailureHandler;

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring().requestMatchers(
                "/css/**","/js/**","/image/**"
        );
    }
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        CustomAccessDeniedHandler accessDeniedHandler=new CustomAccessDeniedHandler();
        accessDeniedHandler.setErrorPage("/errorPage");
        return accessDeniedHandler;
    }
    @Bean
    public CustomAuthenticationEntryPoint customAuthenticationEntryPoint() {
        CustomAuthenticationEntryPoint customAuthenticationEntryPoint=new CustomAuthenticationEntryPoint();
        customAuthenticationEntryPoint.setErrorPage("/errorPage");
        return customAuthenticationEntryPoint;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf().disable();
        http.headers().frameOptions().disable();
        http
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/oauth_login", "/loginFailure", "/h2-console/**",
                                        "/","/login","/signUp","/errorPage","/test").permitAll()
                                .requestMatchers("/profile","/profile/edit").hasRole("USER")
                                .requestMatchers("/notice").hasRole("USER")
                                .anyRequest().authenticated()
                )
                .oauth2Login(oauth2Login ->
                                oauth2Login
                                        .loginPage("/login")
                                        .authorizationEndpoint(authorizationEndpoint ->
                                                        authorizationEndpoint
                                                                .baseUri("/oauth2/authorize-client/**") // oauth2 접근시 사용할 uri 설정(ex.구글 로그인 버튼 href 경로)
                                        )
                                        .defaultSuccessUrl("/")
                                        .userInfoEndpoint()
                                        .userService(principalOauth2UserService) // scope 설정 안하면 구글 로그인은 해당 부분이 실행되지 않는 문제 발생
                                        .and()
                                        .successHandler(customAuthenticationSuccessHandler)
                                        .failureHandler(customAuthenticationFailureHandler)
                            )
                .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(customAuthenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler());
        return http.build();
    }


}
