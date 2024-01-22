---
created: 2023-11-26 07:42:55
---
#QnA/해결 #Dev/Spring/SpringSecurity/QnA 

# JWT와 세션 기반 인증 방식의 차이
- JWT→ 토근 기반 인증 방식
그냥 이렇게 이해하기엔 어렵다. 보통 JWT 인증 방식과 세션 기반 인증 방식 둘 중에 하나를 선택해서 사용하므로 이 두개의 차이점으로 이해하자.

> 세션과 쿠키 그리고 토큰이 정확히 뭔지는 [[2022-07-06-로그인 처리1]]에 정리해줌
> 토큰=랜덤값
## 세션 기반 인증 방식
1. 로그인 프로세스
    - 사용자가 아이디와 비밀번호로 로그인을 시도합니다.
    - 서버는 사용자의 정보를 확인하고, **세션 ID를 생성하여 서버에 저장하고 클라이언트에게 전달합니다.**
2. 세션 저장
    - **서버는 세션 ID를 사용자 정보와 함께 메모리에 저장하거나 데이터베이스에 저장합니다.**
    > 메모리에 저장= HashMap 등의 자료구조를 사용해 그 안에 저장한다는것
    > 데이터베이스에 저장=JPA로 데이터베이스로 저장한다는것
    
    - 클라이언트는 세션 ID를 쿠키에 저장하거나 URL의 일부로 계속 전송합니다.
3. 세션을 통한 인증
    - 사용자의 각 요청마다 세션 ID가 함께 전송되며, 서버는 세션 ID를 사용하여 사용자를 식별합니다.
4. 로그아웃
    - 사용자가 로그아웃하면, 서버는 해당 세션을 무효화하고 삭제합니다.

- 코드 예시
	```Java
// 로그인 컨트롤러
@RestController
public class LoginController {

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password, HttpSession session) {
        // 여기서는 간단하게 username과 password를 확인하여 로그인 성공 시 세션에 사용자 정보를 저장하는 것으로 가정
        if (isValidUser(username, password)) {
            session.setAttribute("username", username);
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<String> getDashboard(HttpSession session) {
        // 세션에서 사용자 정보를 가져와서 확인
        String username = (String) session.getAttribute("username");
        if (username != null) {
            return ResponseEntity.ok("Welcome to the dashboard, " + username + "!");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        // 세션에서 사용자 정보를 삭제하여 로그아웃
        session.invalidate();
        return ResponseEntity.ok("Logout successful");
    }

    private boolean isValidUser(String username, String password) {
        // 간단한 사용자 검증 로직
        return "user".equals(username) && "password".equals(password);
    }
}
```

## JWT 토큰 인증 방식
1. 로그인 프로세스
    - 사용자가 아이디와 비밀번호로 로그인을 시도합니다.
    - 서버는 사용자의 정보를 확인하고, **액세스 토큰과 리프레시 토큰을 생성하여 클라이언트에게 전달합니다.**
2. 토큰 저장
    - **클라이언트는 받은 토큰을 안전한 곳에 저장합니다. 일반적으로는 브라우저의 쿠키나 로컬 스토리지에 저장됩니다.**
3. 토큰을 통한 인증
    - 사용자의 각 요청마다 토큰이 함께 전송되며, 서버는 토큰을 검증하여 사용자를 인증합니다.
4. 토큰 갱신
    - 액세스 토큰이 만료된 경우, 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다.
5. 로그아웃
    - 클라이언트가 로그아웃하면, 서버에서는 토큰을 무효화하거나 만료시킵니다.

- 코드 예제
	```java
// 로그인 컨트롤러
@RestController
public class LoginController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        // 여기서는 간단하게 username과 password를 확인하여 로그인 성공 시 토큰을 생성하는 것으로 가정
        if (isValidUser(username, password)) {
            final UserDetails userDetails = new User(username, password, new ArrayList<>());
            final String token = jwtTokenUtil.generateToken(userDetails);
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<String> getDashboard(@RequestHeader("Authorization") String token) {
        // 토큰을 검증하고, 사용자 정보를 가져와서 확인
        String username = jwtTokenUtil.extractUsername(token);
        if (username != null) {
            return ResponseEntity.ok("Welcome to the dashboard, " + username + "!");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
        }
    }

```

## jwt 특징 정리
- 세션 인증 방식은 세션을 서버와 클라이언트 둘다 저장하고 있고, jwt 토큰은 클라이언트만 저장하고 있으므로 서버 부하가 덜함
	1. **상태 없음 (Stateless)**: 서버가 세션 상태를 유지하지 않습니다. 클라이언트는 각 요청에서 필요한 모든 정보를 토큰에 담아 전송합니다.
	2. **무상태성**:  서버가 클라이언트의 상태를 저장하고 있지 않고 토큰의 복호화만 함
	3. **확장성 및 분산 환경 지원**: 서버의 부하가 감소하고, 여러 서버에서도 쉽게 확장 가능합니다. 각 서버는 독립적으로 토큰을 검증할 수 있습니다.
- 서버에 저장할 필요가 없어 인증시에만 단순히 클라이언트가 토큰만 전달하면 되므로 여러 애플리케이션에서 사용이 수월함
	3. **애플리케이션 간 인증 (Cross-Origin Authentication)**: 여러 독립적인 애플리케이션 사이에서도 토큰을 통한 인증이 용이하며, 동일한 토큰을 사용하여 여러 서비스에 접근할 수 있습니다.
	4. **안전한 데이터 전송**: 토큰은 주로 `JSON Web Token (JWT) `형식을 취하며, 안전한 방식으로 정보를 전송할 수 있습니다.
## 결론
- jwt 토큰은 클라이언트와 서버가 분리되어있는 `REST API`방식(무상태성이 필수요소)에서 사용함
- 그러므로 `jsp`, `thymeleaf`같은 템플릿 엔진으로 구현하는 프로젝트에서는 jwt 토큰을 구현할 의미가 없음.

> - 템플릿 엔진: `SSR(Server Side Rendering)`방식을 사용함
> - `SSR(Server Side Rendering)`: 서버에서 페이지를 결정해주는 방식. 스프링 Controller에서 View를 정하게 된다는 패러다임으로 **클라이언트와 서버가 강결합 되어있는 방식을 의미함. (무상태성 아님)**

- jwt 토큰을 처음에 공부할때 `thymeleaf`를 사용해서 jwt를 구현하려고 했을때 생긴 궁금증: [클라이언트 헤더에 jwt 토큰값을 전달하고 클라이언트는 해당 jwt 토큰값을 저장해놓고 있어야되는데 `thymeleaf`를 사용해서는 클라이언트가 jwt 토큰값을 저장할 방법이 애매했음.](https://stir.tistory.com/275)

