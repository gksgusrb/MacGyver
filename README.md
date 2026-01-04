# ASCII-Art Cloud: Redis 기반 무비밀번호 인증 아스키아트 공유 플랫폼

사용자가 아트를 생성하고 공유할 수 있는 웹 서비스입니다.
이메일 인증 기반 로그인, 작품 공개/비공개 설정, 검색·정렬 기능을 중심으로
백엔드 구조와 배포까지 경험하는 것을 목표로 한 개인 프로젝트입니다.


# 프로젝트 개요

프로젝트명: ASCII-Art Cloud: Redis 기반 무비밀번호 인증 아스키아트 공유 플랫폼

형태: 개인 프로젝트 (1인)

## 목적

Spring Boot 기반 백엔드 실전 연습

인증 / 보안 / 데이터 처리 / 배포 전 과정 경험

아트 생성 및 공유 플랫폼 구현

형태: 개인 프로젝트 (1인)

# 주요 기능

## 회원 & 인증

이메일 인증번호 기반 로그인 (비밀번호 없음)

Redis를 이용한 인증번호 저장 및 TTL 관리

로그인 / 로그아웃

회원 본인 여부에 따른 접근 제어

## 작품(아트) 기능

작품 등록 / 수정 / 삭제

공개 / 비공개 설정

내 작품 페이지 (마이페이지)

타인 공개 작품 열람

키워드 검색

정렬 (최신순 등)



## 기타

CSRF 토큰 처리

예외 처리 (400, 401, 403, 500)

개발 / 운영 환경 분리

# 기술 스택

## Backend

Java 21
:LTS 버전으로 안정성이 높고, Virtual Threads 등 최신 JVM 기능을 활용할 수 있어 장기적인 확장성을 고려하여 선택

Spring Boot 3.x : Spring 애플리케이션 구조를 경험하고자 선택

Spring Security 6 : 세션 기반 인증과 권한 제어를 직접 설계하며 보안 흐름을 깊이 이해하기 위해 도입

Spring Data JPA : 반복적인 CRUD 코드 감소와 도메인 중심 설계를 통해 비즈니스 로직에 집중하기 위해 사용

Redis (인증번호) : 인증번호는 휘발성 데이터이므로 RDB 대신 메모리 DB를 선택
  TTL(Time To Live)을 활용해 인증번호 자동 만료 기능 구현

PostgreSQL (운영) : Fly.io와의 호환성이 뛰어나며, JSON 컬럼 등 확장성 있는 데이터 처리에 적합하여 운영 DB로 선택

H2 (개발)

## DevOps / Infra

Docker : 개발·운영 환경 차이를 줄이고, 동일한 실행 환경을 보장하기 위해 컨테이너 기반 배포 도입

Fly.io : 글로벌 리전 기반 배포와 PostgreSQL/Redis 연동이 용이하여 실제 서비스 환경에 가까운 배포 경험을 위해 선택

GitHub Actions (CI/CD) : 코드 변경 시 자동 빌드·배포 파이프라인을 구성하여 배포 과정을 자동화

## Frontend

Thymeleaf : Spring MVC와의 자연스러운 연동을 통해 서버 사이드 렌더링 구조를 이해하기 위해 사용

HTML / CSS / JavaScript

# 운영 및 인프라 전략

### 1. CI/CD 파이프라인 구축
- **GitHub Actions**를 활용하여 `main` 브랜치 푸시 시 자동 빌드 및 **Fly.io** 배포 자동화를 구현했습니다.
- 이를 통해 수동 배포의 실수를 방지하고, 코드 수정부터 반영까지의 사이클을 단축하여 운영 효율화를 경험했습니다.

### 2. Redis 기반 세션 및 데이터 관리
- 인증번호의 휘발성 특성을 고려하여 **Redis의 TTL(Time To Live)** 기능을 활용, 효율적인 메모리 관리와 자동 만료를 구현했습니다.

# 인증 흐름

1. 이메일 입력

2. 인증번호 발송

3. Redis에 인증번호 저장 (TTL 5분)

4. 인증번호 검증

5. 로그인 완료

# Troubleshooting

## 1. "Spring Security의 기본 인증 전략(DaoAuthenticationProvider) 커스텀을 통한 무비밀번호 인증 구현"

## 문제

이메일 인증번호 방식 로그인을 구현했으나
Spring Security 기본 설정으로 인해 비밀번호 입력을 요구함

## 원인

Spring Security의 기본 인증 흐름은
UsernamePasswordAuthenticationFilter 기반

비밀번호 없는 인증 시나리오를 고려하지 않은 설정

## 해결

인증 로직을 컨트롤러 + 서비스 계층에서 직접 처리

/api/auth/** 경로를 permitAll()로 허용

세션 기반 인증으로 전환하여 Security 흐름과 충돌 제거

## 배운 점

Spring Security는 “기본 동작을 바꾸지 않으면 반드시 개입한다”

커스텀 인증은 필터 흐름 이해가 필수

## 2. Spring Security로 인해 API가 401/403으로 차단되던 문제

## 문제

인증번호 발송 API 호출 시 401 오류 발생
Postman 테스트 자체가 불가능

## 원인

Spring Security 기본 정책: 로그인하지 않으면 API 접근 불가

## 해결

.anyRequest().permitAll()
→ 이후 단계에서

java
코드 복사
.anyRequest().authenticated()
로 다시 보안 강화
## 배운 점

개발 초기에는 열고, 기능이 안정되면 닫는다

보안은 “꺼두는 것”이 아니라 “단계적으로 설계하는 것”

## 3. 모든 권한이 막혀버린 문제

## 문제

로그인부터 회원가입 외 모든 기능이 403 에러를 보내며 먹통이 되었다
Spring Security 에서 모든 권한을 열고 시도했지만 그래도 막히는 문제

## 원인

Spring Security의 문제가 아닌 MemberRole 을 만들고 권한도 설정했으나
Member member = Member.builder() 에서  .role(MemberRole.USER) 를 작성하지 않아 생긴 문제였다


## 해결

.role(MemberRole.USER) 를 추가해주어서 권한을 설정하니 해결된 문제였다

## 배운 점

Spring Security말고도 내가 백엔드에서 설정한 권한으로 권한을 통제할수있고 
시큐리티로 편하게 할수 있는 것을 배웠다
## 4. 모든 권한이 막혀버린 문제

## 문제

! [rejected] main -> main (fetch first)

## 원인

원격 저장소와 로컬 브랜치 이력 불일치

README를 GitHub 웹에서 수정한 이력 존재

## 해결

git push --set-upstream origin main
## 배운 점

1인 개발이었지만 이런식으로 협업 개발시 발생할수있는 문제 보통은 내 코드를 업데이트한다음
업로드 하겠지만 1인개발이니 그냥 무시하고 업로드했다