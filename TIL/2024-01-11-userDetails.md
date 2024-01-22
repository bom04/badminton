---
created: 2024-01-11 11:48:16
---
# UserDetails 인터페이스
> spring security가 사용자의 인증을 처리하는데 필요한 사용자 정보를 담고있는 인터페이스

- `Authentication authentication = SecurityContextHolder.getContext().getAuthentication();`
	- 세션에 사용자 정보를 저장하는 Authentication 객체임
	- 여기에 사용자 정보를 저장한다면 기본적으로 `userDetails`가 저장되는데, 만약 내가 커스텀한 유저의 정보를 추가로 저장해서 나중에도 꺼내서 써야 한다면 `userDetails` 인터페이스를 implements한 클래스를 만들어서 그 클래스를 사용하는게 훨씬 편하고 간단하다!


