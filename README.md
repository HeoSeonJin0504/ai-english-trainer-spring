# AI English Trainer — Backend (Spring Boot)

OpenAI GPT API와 Google Cloud TTS를 활용한 AI 기반 영어 학습 플랫폼의 백엔드 서버입니다.

영어 단어를 입력하면 예문·의미·유의어·반의어를 자동 생성하고, TOEIC/영작 문제 출제, 음성 변환, AI 챗봇 기능을 제공합니다.

---

## 주요 기능

- 회원가입 / 로그인 (JWT + httpOnly Cookie)
- 소셜 로그인 (Google, Kakao, Naver OAuth2)
- 단어 저장 및 예문·의미·유의어·반의어 자동 생성 (GPT)
- TOEIC Part 5/6/7 및 영작 문제 자동 출제 (GPT)
- Google Cloud TTS 음성 변환 (미설정 시 Web Speech API로 대체)
- AI 영어 학습 챗봇 (대화 세션 관리)
- 단어·예문·문제 저장 및 관리 (MySQL)

---

## 기술 스택

| 구분 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.5.9 |
| Database | MySQL 8.0 + Spring Data JPA (Hibernate) |
| Auth | Spring Security + JWT + OAuth2 (httpOnly Cookie) |
| AI | OpenAI GPT API (gpt-4o-mini) |
| TTS | Google Cloud Text-to-Speech |
| Build | Gradle |

---

## 프로젝트 구조

```
src/main/java/com/example/aienglishtrainer/
├── config/        # 설정 (Security, CORS, OpenAI, TTS)
├── controller/    # API 컨트롤러
├── service/       # 비즈니스 로직
├── repository/    # JPA Repository
├── entity/        # JPA 엔티티
├── dto/           # 요청/응답 DTO
├── security/      # JWT 필터, Provider, SecurityUtil
├── oauth/         # OAuth2 소셜 로그인 처리
└── exception/     # 전역 예외 처리
```

---

## API 엔드포인트

| 구분 | 엔드포인트 | 설명 |
|------|-----------|------|
| 인증 | `POST /api/auth/signup` | 회원가입 |
| | `POST /api/auth/login` | 로그인 |
| | `POST /api/auth/logout` | 로그아웃 |
| | `GET /api/auth/me` | 내 정보 조회 |
| | `GET /oauth2/authorization/{provider}` | 소셜 로그인 (google·kakao·naver) |
| 단어 | `GET/POST /api/words` | 단어 목록 조회 / 저장 |
| | `GET/DELETE /api/words/{id}` | 단어 상세 조회 / 삭제 |
| 예문 | `GET/POST /api/examples` | 예문 목록 조회 / 저장 |
| | `GET/DELETE /api/examples/{id}` | 예문 상세 조회 / 삭제 |
| 문제 | `GET/POST /api/questions` | 문제 목록 조회 / 저장 |
| | `GET/DELETE /api/questions/{id}` | 문제 상세 조회 / 삭제 |
| 생성 | `POST /api/generate/examples` | 예문 AI 생성 |
| | `POST /api/generate/questions` | TOEIC/영작 문제 AI 생성 |
| TTS | `POST /api/tts/speak` | 텍스트 음성 변환 |
| | `GET /api/tts/status` | TTS 서비스 상태 확인 |
| 챗봇 | `POST /api/chat/message` | 챗봇 메시지 전송 |
| | `GET /api/chat/conversations` | 대화 목록 조회 |
| | `GET /api/chat/history/{id}` | 대화 히스토리 조회 |
| | `DELETE /api/chat/{id}` | 대화 삭제 |

---

## 설치 및 실행

**필수 환경**

- Java 17 이상
- MySQL 8.0 이상

### 1. 데이터베이스 생성

```sql
CREATE DATABASE english_trainer;
```

### 2. 환경변수 설정

`application-local.yaml.example`을 참고하여 `src/main/resources/application-local.yaml`을 생성합니다.

```
src/main/resources/
├── application.yaml
├── application-local.yaml        ← 직접 생성 (Git 제외)
└── application-local.yaml.example  ← 참고용 템플릿
```

> ⚠️ `application-local.yaml`은 절대 Git에 커밋하지 마세요.

### 3. 소셜 로그인 설정 (선택)

각 플랫폼 개발자 콘솔에서 앱을 생성하고 Redirect URI를 등록합니다.

| Provider | Redirect URI |
|----------|-------------|
| Google | `http://localhost:8080/login/oauth2/code/google` |
| Kakao | `http://localhost:8080/login/oauth2/code/kakao` |
| Naver | `http://localhost:8080/login/oauth2/code/naver` |

발급받은 Client ID / Secret을 `application-local.yaml`의 `spring.security.oauth2.client.registration` 하위에 입력합니다.

### 4. Google Cloud TTS 설정 (선택)

1. [Google Cloud Console](https://console.cloud.google.com/)에서 프로젝트 생성
2. Text-to-Speech API 활성화
3. 서비스 계정 생성 후 JSON 키 다운로드
4. `config/google-credentials.json`으로 저장

> TTS를 설정하지 않아도 프론트엔드의 Web Speech API로 자동 대체됩니다.

### 5. 실행

IntelliJ IDEA에서 `AiEnglishTrainerApplication.java`를 실행합니다.

```
실행 구성: Spring Boot
Active Profile: local
```

---

## 인증 방식

JWT를 httpOnly Cookie로 발급하여 XSS 공격을 방지합니다.

```
일반 로그인: POST /api/auth/login → accessToken Cookie 발급
소셜 로그인: /oauth2/authorization/{provider} → OAuth2 콜백 → accessToken Cookie 발급
이후 요청:  Cookie 자동 전송 → JwtAuthenticationFilter에서 검증
```

---

## 관련 저장소

| 구분 | 링크 |
|------|------|
| 백엔드 (Spring Boot) | 현재 저장소 |
| 프론트엔드 (React) | https://github.com/HeoSeonJin0504/ai-english-trainer-front |
| 백엔드 (Node.js) | https://github.com/HeoSeonJin0504/ai-english-trainer-node |