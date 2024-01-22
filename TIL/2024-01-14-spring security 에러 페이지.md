---
created: 2024-01-14 06:47:19
---
# spring security의 에러 페이지
- 기존
	- 에러나 오류가 발생하면 `whitelabel error page`가 나와서 무슨 오류인지 클라이언트가 인지할 수 있음
- spring security
	- 에러나 오류가 발생하면 spring security에서 필터링되기 때문에 개발자가 따로 설정하지 않는이상 어떤 오류 페이지도 나오지 않아 어떤 오류인지 클라이언트가 인지할 수 없음

## 설정 방법
1. 직접 설정하는 방법
	- Custom ExceptionHandler
		```java
		@ControllerAdvice
		public class CustomExceptionHandler {
		
		    @ExceptionHandler(Exception.class)
		    public String handleException(Exception ex, Model model) {
		        // 여기에 모델에 필요한 정보를 추가하거나 로깅
		        model.addAttribute("error", ex.getMessage());
		        return "error"; // 뷰 error.html을 보여줌
		    }
		}
		```
		- `@ControllerAdvice`: spring에서 전역 컨트롤러에 적용되는 예외 처리를 담당하는 클래스에 사용하는 어노테이션으로, 이 어노테이션을 사용하면 여러 컨트롤러에서 발생하는 예외를 한 곳에서 일괄적으로 처리할 수 있음
	- 이 방법은 아무 곳에서나 예외 발생시키면 저 클래스가 다 받아서 처리한다는 장점이 있지만, 수많은 인증 예외와 인가 예외를 하나하나 처리할 수 없음

1. 인증 예외와 인가 예외
	> 인증 예외←–>인가 예외 완전히 다름!
	
	- 인증 예외
		> anonymous가 접근하려고 할때 발생하는 예외
		
		```java
		public class CustomAccessDeniedHandler implements AccessDeniedHandler {  
		    private String errorPage;  
		  
		    @Override  
		    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {  
		        String deniedUrl=errorPage+"?exception="+accessDeniedException.getMessage();  
		        response.sendRedirect(deniedUrl);  
		    }  
		  
		    public void setErrorPage(String errorPage) {  
		        this.errorPage=errorPage;  
		    }  
		}
		```

	- 인가 예외
		> 인증은 성공했지만 권한이 없는 인가 예외일때 발생
		
		[[2023-02-28-실전 프로젝트-인증 프로세스 form 인증 구현#Access Denied]]