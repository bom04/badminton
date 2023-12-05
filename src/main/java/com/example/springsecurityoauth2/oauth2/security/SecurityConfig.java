package com.example.springsecurityoauth2.oauth2.security;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
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

@Configuration
@PropertySource("application-oauth2.properties")
@RequiredArgsConstructor
public class SecurityConfig {

    private final PrincipalOauth2UserService principalOauth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.headers().frameOptions().disable();
        http
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/css/**","/js/**","/notice","/login","/oauth_login", "/loginFailure", "/","/h2-console/**").permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2Login(oauth2Login ->
                        oauth2Login
                                .loginPage("/oauth_login")
                                .authorizationEndpoint(authorizationEndpoint ->
                                        authorizationEndpoint
//                                                .authorizationRequestResolver(customAuthorizationRequestResolver())
                                                .baseUri("/oauth2/authorize-client/**") // oauth2 접근시 사용할 uri 설정(ex.구글 로그인 버튼 href 경로)
//                                                .authorizationRequestRepository(authorizationRequestRepository())
                                )
//                                .tokenEndpoint(tokenEndpoint ->
//                                        tokenEndpoint.accessTokenResponseClient(accessTokenResponseClient())
//                                )
                                .defaultSuccessUrl("/loginSuccess")
                                .failureUrl("/loginFailure")
                                .userInfoEndpoint()
                                .userService(principalOauth2UserService) // scope 설정 안하면 구글 로그인은 해당 부분이 실행되지 않는 문제 발생
                );
        return http.build();
    }

//    private OAuth2AuthorizationRequestResolver customAuthorizationRequestResolver() {
//        return new CustomAuthorizationRequestResolver(clientRegistrationRepository(), "/oauth2/authorize-client");
//    }


    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
        return new HttpSessionOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
        DefaultAuthorizationCodeTokenResponseClient accessTokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
        return accessTokenResponseClient;
    }

    // additional configuration for non-Spring Boot projects
    private static List<String> clients = Arrays.asList("google", "facebook");

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> registrations = clients.stream()
            .map(c -> getRegistration(c))
            .filter(registration -> registration != null)
            .collect(Collectors.toList());

        return new InMemoryClientRegistrationRepository(registrations);
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService() {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository());
    }

    private static String CLIENT_PROPERTY_KEY = "spring.security.oauth2.client.registration.";

    @Autowired
    private Environment env;

    private ClientRegistration getRegistration(String client) {
        String clientId = env.getProperty(CLIENT_PROPERTY_KEY + client + ".client-id");

        if (clientId == null) {
            return null;
        }

        String clientSecret = env.getProperty(CLIENT_PROPERTY_KEY + client + ".client-secret");
        if (client.equals("google")) {
            return CommonOAuth2Provider.GOOGLE.getBuilder(client)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
        }
        if (client.equals("facebook")) {
            return CommonOAuth2Provider.FACEBOOK.getBuilder(client)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
        }
        return null;
    }

}
