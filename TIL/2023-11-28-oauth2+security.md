---
created: 2023-11-28 11:15:25
---
#Dev/Spring/SpringSecurity 

# 참고한 사이트
## api 키와 oauth2.0 클라이언트 ID 발급받기
- [OAuth 2.0 개념과 Google OAuth2.0 세팅부터 스프링부트로 구현-1](https://darrenlog.tistory.com/38)
## 구현하기
- [스프링 시큐리티 5 – OAuth2 로그인](https://www.baeldung.com/spring-security-5-oauth2-login)
	- [해당 github 코드=내 github의 TIL>spring code>spring-security-oauth2에 정리](https://github.com/eugenp/tutorials/tree/master/spring-security-modules/spring-security-oauth2)
- [(Spring Boot) OAuth 2.0 로그인 (구글 로그인)](https://chb2005.tistory.com/182)

# 코드 정리
- TIL>spring code>spring-security-oauth2 폴더
	- 다만 그 안의 `oauth2request` 패키지는 아님
	- `oauth2` 패키지가 구현한 코드임

# 정리
> 여기서는 google 로그인을 기준으로 설명함


- 의존성 추가
	```gradle
	implementation 'org.springframework.boot:spring-boot-starter-oauth2-client'
	```

- application.properties
	```application
	spring.security.oauth2.client.registration.google.client-id=발급받은 id
	spring.security.oauth2.client.registration.google.client-secret=발급받은 비밀번호
	spring.security.oauth2.client.registration.google.scope=profile,email

	#  구글 외 provider에서는 각 provider 플랫폼에서 설정한 redirect-uri를 동일하게 입력해줘야됨
	spring.security.oauth2.client.registration.kakao.redirect-uri=http://localhost:8081/login/oauth2/code/kakao
	```

> google 로그인은 저 scope 설정을 필수적으로 해야됨. [안그러면 내가 커스텀한 `userService`가 제대로 동작하지 않는 문제가 발생함](https://velog.io/@beenz/Spring-Security-OAuth2.0-%EC%97%90%EC%84%9C-Google-Login%EB%A7%8C-OAuth2UserService%EC%9D%98-%EC%BB%A4%EC%8A%A4%ED%85%80-%EA%B5%AC%ED%98%84%EC%B2%B4-%EB%A1%9C%EC%A7%81-%ED%83%80%EC%A7%80-%EC%95%8A%EB%8A%94-%EB%AC%B8%EC%A0%9C)

- 리디렉션 URI 설정
	- 클라이언트 ID를 발급받을때 승인된 리디렉션 URI를 `http://localhost:8081/login/oauth2/code/google`로 설정함 → 즉 `구글 로그인` 버튼을 눌렀을때 해당 URI로 로그인이 진행되도록 설정했다는 뜻

- securityConfig.class
	```java
	http
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/oauth_login", "/loginFailure", "/","/h2-console/**").permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2Login(oauth2Login ->
                        oauth2Login
                                .loginPage("/oauth_login")
                                .authorizationEndpoint(authorizationEndpoint ->
                                        authorizationEndpoint
                                                .baseUri("/oauth2/authorize-client/**") // oauth2 접근시 사용할 uri 설정(ex.구글 로그인 버튼 href 경로)
                                )
                                .defaultSuccessUrl("/loginSuccess")
                                .failureUrl("/loginFailure")
                                .userInfoEndpoint()
                                .userService(principalOauth2UserService) // scope 설정 안하면 구글 로그인은 해당 부분이 실행되지 않는 문제 발생
                );
        return http.build();
	```

- login.html
	```java
	<a th:href="@{/oauth2/authorize-client/google}">
	    <img src="https://developers.google.com/identity/images/g-logo.png" alt="Google 로그인" />
	</a>
	```
> href는 위에서 설정한 uri와 맞추기

- controller.java
	```java
	@Controller  
	public class LoginController {  
	    private static final String authorizationRequestBaseUri = "oauth2/authorize-client";

		@GetMapping("/loginSuccess")  
		public String getLoginInfo(Model model, OAuth2AuthenticationToken authentication) {  
		    // 로그인한 유저 가져오기  
		    OAuth2User oauth2User = authentication.getPrincipal();  
		    // 로그인한 유저 닉네임 가져오기  
		    System.out.println("client: "+oauth2User.getAttribute("name"));
		    ...
		}
	}
	```

- PrincipalDetails.java
	```java
	// form 방식으로 회원가입한 사람들은 UserDetails 타입으로 저장되고
	// oauth로 가입한 사람들은 OAuth2User 타입으로 저장됨
	public class PrincipalDetails implements UserDetails, OAuth2User {
		  private User user;
	    // oauth 로그인을 통해서 받은 유저 정보를 그대로 담아 return 해주기 위한 변수
	    private Map<String, Object> attributes;
	    
		public PrincipalDetails(User user, Map<String, Object> attributes) {
	        this.user = user;
	        this.attributes = attributes;
	    }
	    ...
	}
	```

- service
	- **`providerId`: 소셜에서 사용자에게 제공하는 pk 값**
	- `loginId`: `provider+providerId`로 합친 값
	>  왜 유저를 `email`로 구별하지 않고 굳이 `provider+providerId`인 `loginId`를 만들어서 사용할까?
	>  → 당장 나만해도 카카오톡 소셜 이메일을 네이버 이메일로 쓰는등 이렇게 섞어 쓰는 경우가 많아서.
	>  → 이러면 카톡 소셜 이메일=네이버 이메일이 같은 상황이 발생하지만 둘의 플랫폼인 provider이 다르기 때문에 서로 다른 유저로 분류해야됨. 
	
	```java
	@Service
	@RequiredArgsConstructor
	public class PrincipalOauth2UserService extends DefaultOAuth2UserService {
	
	    private final UserRepository userRepository;
	
	    @Override
	    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
	        // 로그인한 유저 정보 전부 들어있음
	        OAuth2User oAuth2User = super.loadUser(userRequest);
	        System.out.println("oAuth2User.getAttributes(): "+oAuth2User.getAttributes());
	
	        String provider = userRequest.getClientRegistration().getRegistrationId();
	        String providerId = oAuth2User.getAttribute("sub"); // 구글인지 카카오인지 등
	        String loginId = provider + "_" +providerId;
	
	        Optional<User> optionalUser = userRepository.findByLoginId(loginId);
	        User user;
	
	        if(optionalUser.isEmpty()) {
	            user = User.builder()
	                    .loginId(loginId)
	                    .nickname(oAuth2User.getAttribute("name"))
	                    .provider(provider)
	                    .providerId(providerId)
	                    .role(UserRole.USER)
	                    .build();
	            userRepository.save(user);
	        } else {
	            user = optionalUser.get();
	        }
	
	        return new PrincipalDetails(user, oAuth2User.getAttributes());
	    }
	}
	```

