# MSA 기반 커뮤니티 및 사용자 관리 시스템

## 프로젝트 소개

이 프로젝트는 마이크로서비스 아키텍처(MSA)를 기반으로 개발된 시스템으로, 사용자 관리(User Service)와 커뮤니티 관리(Community Service) 두 개의 핵심 서비스로 구성되어 있습니다. Spring Boot와 Spring Cloud 기술 스택을 활용하여 개발되었으며, Docker 컨테이너화 및 GitHub Actions를 통한 CI/CD 파이프라인을 구축하여 확장 가능하고 유지보수가 용이한 시스템을 구현했습니다.

## 주요 서비스 구성

### User Service (msa-user)
- **역할**: 사용자 인증, 회원가입, 구독 관리
- **포트**: 8088
- **주요 기능**:
  - JWT 기반 사용자 인증 (로그인, 로그아웃)
  - 회원가입 및 이메일 인증
  - 구독 상태 관리 및 광고 제어
  - Redis를 활용한 토큰 관리

### Community Service (msa-community)
- **역할**: 게시글, 댓글, 좋아요 관리
- **포트**: 8081
- **주요 기능**:
  - 자산(Asset) 기반 게시글 관리
  - 댓글 CRUD 기능
  - 게시글 좋아요 기능
  - 사용자별 게시글 및 좋아요 조회

## 기술 스택

- **백엔드**: Spring Boot 3.4.2, Java 17
- **보안**: Spring Security, JWT
- **데이터베이스**: MySQL, Redis
- **서비스 통신**: WebClient
- **빌드/배포**: Docker, Docker Compose, GitHub Actions
- **서비스 디스커버리**: Eureka (외부 서버 활용)
- **인프라**: AWS EC2

## 주요 구현 사항

1. **마이크로서비스 구조**
   - 독립적인 서비스로 사용자와 커뮤니티 기능 분리
   - Eureka 서비스 디스커버리를 통한 서비스 등록 및 발견

2. **인증 및 보안**
   - JWT 토큰 기반 인증 체계
   - Redis를 활용한 토큰 저장 및 관리
   - API 요청 간 사용자 정보 전달 (X-Auth-User 헤더)

3. **데이터 관리**
   - JPA를 활용한 데이터 접근 계층
   - 트랜잭션 관리를 통한 데이터 일관성 유지
   - Redis를 활용한 캐싱 전략

4. **자동화된 CI/CD 파이프라인**
   - GitHub Actions를 통한 빌드 및 배포 자동화
   - Docker Compose를 통한 서비스 관리

## 아키텍처 다이어그램

```
클라이언트 → API Gateway(34.210.11.121:8080) → 마이크로서비스
                          ↓
                Eureka Server(34.210.11.121:8761)
                          ↓
          ┌───────────────┴───────────────┐
          ↓                               ↓
   User Service(8088)             Community Service(8081)
          ↓                               ↓
      MySQL/Redis                     MySQL/Redis
```

## 배포 환경

AWS EC2 인스턴스에 Docker Compose를 통해 배포되며, GitHub Actions 워크플로우를 통한 자동 배포가 구성되어 있습니다. 코드 변경 시 main 브랜치에 병합되면 자동으로 빌드 및 배포가 진행됩니다.

## 주요 API 기능

### User Service
- 회원가입 및 로그인
- 이메일 인증
- 구독 상태 관리
- 사용자 정보 조회

### Community Service
- 게시글 CRUD
- 댓글 CRUD
- 게시글 좋아요
- 사용자별 게시글 및 좋아요 조회

## 개발자 참고사항

- 각 서비스는 독립적으로 빌드 및 배포 가능
- 서비스 간 통신은 Eureka와 API Gateway를 통해 이루어짐
- 환경 변수를 통한 설정 관리
- Redis 연결 정보 및 기타 민감 정보는 별도 관리 필요

## 시스템 운영

시스템은 Docker Compose를 통해 관리되며, 각 서비스는 독립적인 컨테이너로 실행됩니다. 서비스 로그는 Docker logs 명령을 통해 확인할 수 있으며, 배포 과정에서 자동으로 수집됩니다.
