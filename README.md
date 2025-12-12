강사님 피드백

1\. cleanEmail 을 선언해 놓고 이메일을 입력받는 코드에만 적용시켰음 강사님이 전체적으로 적요ㅇ시키는것이 좋다고 피드백//

2\. 지금 전부 런타임으로 되어있음

3\. 모든 권한이 열려있는 상태임으로 닫아줄것/



.anyRequest().permitAll()-> 이부분을 .anyRequest().authenticated() 로 바꿔줌/

파일 위치 문재//

일단 폼을 구현해 보라 하심//

컨피그 파일을 글로벌 파일이라고 따로만들어서 그안에서 관리 하신다고 하심//

임포트 하는 부분에서 길어지는 문제 명령어랑 중복되며 충돌을 피하려는 자바 시스탬이 경로를 직접 입력해 임포트 하는 문제였음 이름 바꿔주고 대문자 추가해 줌으로써 해결//

보안관련 => 포스트맨으로 테스드 할때 계속 막혀서 잠시껐지만 이제 html 로테스트하기떄문에 다시켬

커큘레이ㅌㅓ 깃허브에서 오픈소스가져와서 해보고 분석해보기





기록

회원가입 기능 구현중
회원가입시 이메일과 아이디를 입력하고 이메일로 인증번호 6자리를 보내 인증하면 회원가입이되는 기능

25/11/04 회원가입 기능을 어느정도 구현한후 테스트중
git 업로드가 원활하지않아 문제해결함

25/11/06 회원가입 기능을 도커로 테스트하는데
docker run -d -p 6379:6379 --name redis redis 를 입력할때 생성은 되지만 연동이 되지않는문제 관리자원한 문제인것 같다

강사님의 조언으로 로컬로 먼저 테스트 하기로함
package com.sbs.tutorial.config 에서 FakeRedisConfig 파일을 만들어 인증번호를 임시저장한다

EmailService 에 Map을 사용하였다기록

회원가입 기능 구현중
회원가입시 이메일과 아이디를 입력하고 이메일로 인증번호 6자리를 보내 인증하면 회원가입이되는 기능

25/11/04 회원가입 기능을 어느정도 구현한후 테스트중
git 업로드가 원활하지않아 문제해결함

25/11/06 회원가입 기능을 도커로 테스트하는데
docker run -d -p 6379:6379 --name redis redis 를 입력할때 생성은 되지만 연동이 되지않는문제 관리자원한 문제인것 같다

강사님의 조언으로 로컬로 먼저 테스트 하기로함
package com.sbs.tutorial.config 에서 FakeRedisConfig 파일을 만들어 인증번호를 임시저장한다

EmailService 에 Map을 사용하였다 (이메일이라는 키에 인증번호라는벨류 설정)
또한 로컬과 나중에 도커Redis를 사용하기 위해 if문으로 구분했다
AuthService 에서도 로컬과 도커를 분리했다 (로컬로 사용하면 자동으로 벨류를 제거하는 기능구현)

로컬로 설정한후 실행했지만 서버가 실행안됨
build.gradle 에 runtimeOnly 'com.h2database:h2' 의존성 주입하라는 정보 -> 실행되는것을 확인함

Postman 으로 POST 로 설정하고 http://localhost:8080/api/auth/send-code?email=나의 이메일@gmail.com 보냄
401 오류 = 찾아보니Spring Security는 기본적으로 로그인해야만 API 사용 가능 으로 설정함 그래서 풀어줘야함
app1/config/SecurityConfig.java 를 추가하고 거기에서 해제
이후 정상적으로 메일이 전달되는것을 확인

http://localhost:8080/api/auth/verify?email=이메일6079@gmail.com\&username=원하는이름\&code=인증번호
500 오류 = 프로그램 내부 문제
정확히 입력했지만 "인증번호가 만료되었거나 존재하지 않습니다." 반환

25/11/07
git push 오류  git add . \&\& git commit -m "work" \&\& git push origin main 를 사용시
! \[rejected]        main -> main (fetch first)
error: failed to push some refs to
이뜨는 문제 이것은 지금 이 push 하려는 작업 말고 다른 방법으로 수정되어 지금 작업과 git에 있는작업이 일치하지않아서 생긴 현상이다 발생하는 이유는 다른 팀원이 먼저 push하거나 나처럼 git 에서 README 를 수정하고 업로드해서 생긴 문제이다
git push 를 그냥 입력하니 git push --set-upstream origin main를 다시한번 입력해보라고 추천해줘서 입력하니 해결되었다



25/11/13 로그인기능 오류 해결

로그인 기능 구현할떄 로그인 이메일이 이상하게 출력되는 문제 단순히 메서드 이름을 잘못 입력했었음



드디어 로그인 기능을 완성함 이후 로그인 유지 세션을 만들어 야겠다 생각함

1.로그인 유지

2.로그아웃 -> 잊고있었음



25/11/18 로그인 유지 테스트 와 로그아웃 기능 구현

로그인 유지기능을 테스트 하기위해

authservice 에다가



        List<GrantedAuthority> authorities =

                List.of(new SimpleGrantedAuthority("ROLE\_USER"));



        UsernamePasswordAuthenticationToken authenticationToken =

                new UsernamePasswordAuthenticationToken(

                        new org.springframework.security.core.userdetails.User(

                                user.getEmail(),

                                "",

                                authorities

                );

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
기능을 넣고

authcontroller 에다가 이렇게 넣어서 확인할 준비를함

// 로그인 유지 확인용 긁어옴 나중에는 지울듯

@GetMapping("/me")

public ResponseEntity<?> me() {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();



    if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {

        return ResponseEntity.status(401).body("로그인되지 않았습니다.");

    }



    Object principal = auth.getPrincipal();



    if (principal instanceof com.sbs.tutorial.app1.domain.user.User user) {

        return ResponseEntity.ok("현재 로그인한 사용자: " + user.getEmail());

    }



    return ResponseEntity.status(401).body("로그인 정보가 올바르지 않습니다.");

}

securityconfig 에다가

                .logout(logout -> logout.disable());

추가함 하지만 시큐리티는 자동으로 아이디 비밀번호 방식을 사용하는대 나는 비밀번호없는 인증번호 방식의 기능이라 자잘한 버그를 일으킴 403 이나 401 그래서 그 기능을끄고 테스트해봄

.httpBasic(httpBasic -> httpBasic.disable())

이 코드를  추가하자 로그인기능은 확정으로 기능했지만 로그인 유지 되고있지않음

그래서 여러코드를 추가해 가면 알아보다가

 .securityContext(security ->

                        security.requireExplicitSave(false))

 .sessionManagement(session ->

                        session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)

                )

를 추가해서 저장 위치를 강제함

    // 로그아웃 기능

    @PostMapping("/logout")

    public ResponseEntity<String> logout(HttpServletRequest request) {



        // 세션 초기화

        request.getSession().invalidate();



        // 시큐리티 인증 초기화

        SecurityContextHolder.clearContext();



        return ResponseEntity.ok("로그아웃 완료");

    }

로그아웃 기능 추가







지금부터는 만든기능을 배포해야함 지금까지 테스트는 포스트 맨으로 했음 이제는 웹 브라우져 에서 실행할것

11/21

브라우저에서 실행중 html이 문제나서 이메일이 전송안된다든가 로그인이 안되는문제

문제를 해결하기 위해 여려 방법을 실행 하던 도중 csrf를 켜서 그런가 하고 여러 방법을 시도함 결국 다시한번 csrf를 끄고 시도해본 결과 MEmberRoll을 설정해 권한을 놓고 적용하지않아 계속 거부되던 문제였다

Member member = Member.builder()            Member member = Member.builder()

        .email(cleanEmail)                               .email(cleanEmail)

        .username(username)                           .username(username)

        .verified(true)                                      .verified(true)

                                                                .role(MemberRole.USER)  <- 이부분 추가 아해서 생긴 문제

        .build();                                              ,build();





12/8

아스키아트 실행도중 권한문제가 발생해 해결하려했지만 안되서 전부 공개로 해놓았지만 파일을 찾지못하는 문제가 발생 알아보니 이름을 잘못쓰는 문제



12/9 

오류 Caused by: java.lang.IllegalStateException: Ambiguous mapping. Cannot map 'asciiController' method

너무많은 메핑이 들어와 어느부분을 참조해야할지 모른다는 오류

컨트롤러에 문재가 생겼음을 확인

&nbsp;   @PreAuthorize("isAuthenticated()")

&nbsp;   @GetMapping("/modify/{id}")

&nbsp;   public String asciiModify(Asciiform asciiform, @PathVariable("id") Integer id, Principal principal) {

&nbsp;...

&nbsp;   @PreAuthorize("isAuthenticated()")

&nbsp;   @GetMapping("/modify/{id}")

&nbsp;   public String asciiModify(@Valid Asciiform asciiform, BindingResult bindingResult,

...

찾아보니 post로 받아야하는데 위에 코드에서 복ㅌ하는 과정에서 get을 post롤 바꾸지 않아서 생긴 문제였다

