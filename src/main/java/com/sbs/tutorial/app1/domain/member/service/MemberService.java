package com.sbs.tutorial.app1.domain.member.service;

import com.sbs.tutorial.app1.domain.member.entity.Member;
import com.sbs.tutorial.app1.domain.member.repository.MemberRepository;
import com.sbs.tutorial.app1.domain.member.entity.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final StringRedisTemplate redisTemplate;
    private final Map<String, String> fakeRedisStorage; //로컬
//authcontroller에서 호출 받아서 authService.verifyCodeAndRegister(email, username, code) 정보를받아옴
    public void verifyCodeAndRegister(String email, String username, String inputCode) {
        String cleanEmail = email.trim();// 변수를 설정해 뛰어쓰기 하더라고 공백을 제거하고 입력받음

        String savedCode = null;
//savedCode 를 널로 설정하고 if문으로 가져옴
        // 상황에 따라서 저장되어있는 정보를꺼내와 savedCode라는 도마에 올려놓음
        if (fakeRedisStorage != null) {
            savedCode = fakeRedisStorage.get("verify:" + cleanEmail);
        } else {
            // prod: Redis에서 꺼냄
            savedCode = redisTemplate.opsForValue().get("verify:" + cleanEmail);
        }
// savedCode 라는 도마위의 상태에 따라 정보가 일치하는지 확인
        if (savedCode == null) {
            throw new RuntimeException("인증번호가 만료되었거나 존재하지 않습니다.");
        }
        if (!savedCode.equals(inputCode)) {
            throw new RuntimeException("인증번호가 일치하지 않습니다.");
        }
// 중간에 멤버리포지터리에서 정보를 대조함
        if (memberRepository.existsByEmail(cleanEmail)) {
            throw new RuntimeException("이미 가입된 이메일입니다.");
        }

        if (memberRepository.existsByUsername(username)) {
            throw new RuntimeException("이미 사용 중인 닉네임입니다.");
        }
// 멤버 리포지터리 에서 public interface MemberRepository extends JpaRepository<Member, Long> 설정해서 기본 CRUD 기능 을 불러와 사용가능한 코드다
        Member member = Member.builder() //맴버 엔티티를 생성하고 저장함
                .email(cleanEmail) //이메일 생성
                .username(username) //이름 생성
                .verified(true)     //인증 완료 된 증명서 생성
                .role(MemberRole.USER)// 유저 권한 생성
                .build();
        memberRepository.save(member); // 그리고 db에 정보 저장 정확히는 db_dev.mv.db
//마지막으로 저장되어있는 인증번호를 찾아서 삭제해 준다
        if (fakeRedisStorage != null) {
            fakeRedisStorage.remove("verify:" + cleanEmail);
        } else if (redisTemplate != null) {
            redisTemplate.delete("verify:" + cleanEmail);
        }
    }
    public Member logincode(String email, String inputCode) {//받은 아이디 코드를 가져옴
        String cleanEmail = email.trim();
        Member member = memberRepository.findByEmail(cleanEmail) //맴버리포지터리에서 Optional<Member> findByEmail(String email); 로 있으면 Optional<Member> 없으면Optional<> 로 가져옴
                .orElseThrow(() -> new RuntimeException("없는 이메일입니다"));
            //orElseThrow 는 Optional<>의 메서드 이다
        // .orElseThrow(() -> new RuntimeException(1)); ==  .예외해라((공백이면) -> new RuntimeException("없는 이메일입니다")을 출력)
        String savedCode;
        if (fakeRedisStorage != null) {
            savedCode = fakeRedisStorage.get("login:" + cleanEmail);
        } else {
            // prod: Redis에서 꺼냄
            savedCode = redisTemplate.opsForValue().get("login:" + cleanEmail);
        }

        if (savedCode == null) {
            throw new RuntimeException("인증번호가 만료되었거나 존재하지 않습니다.");
        }
        if (!savedCode.equals(inputCode)) {
            throw new RuntimeException("인증번호가 일치하지 않습니다.");
        }

        //멤버 룰에서 권한을 가져와서 스프링 시큐리티에 적용시켰다 이유는 비밀번호없는 로그인이라 제공하는 기본적 기능을꺼버렸기 떄문이다
        List<GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority(member.getRole().getValue()));
                                                 //       룰에서      권한을       가져온다는 뜻
        UsernamePasswordAuthenticationToken authenticationToken = //권한을끄고 내가 직접 설정을 가져와 설정할 것임
                new UsernamePasswordAuthenticationToken(
                        new org.springframework.security.core.userdetails.User(
                                member.getEmail(), //위에 모즌건 기본설정이고 여기서부터 맴버에서 이메일을 가져오고
                                "",               //우리는 비밀번호가 없음으로 공백으로 둔다
                                authorities       // 위에서 만든 룰 권한 정보를 가져온다
                        ),
                        null,// 이곳에 비번을 넣지만 없기도 하고 이미 인증했기때문에 불필요
                        authorities
                );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        //모든 것이 인증 완료되면 스프링 시큐리티에 정보를 보내 저장시킴 시큐리티는 따로 검사안함
        if (fakeRedisStorage != null) {
            fakeRedisStorage.remove("login:" + cleanEmail);
        } else if (redisTemplate != null) {
            redisTemplate.delete("login:" + cleanEmail);
        }
        return member;
    }

}