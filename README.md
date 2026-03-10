# AI English Trainer (Backend - Java Spring Boot)

OpenAI GPT API와 Google Cloud TTS를 활용한 AI 기반 영어 학습 플랫폼의 백엔드 서버입니다.

사용자가 입력한 영어 단어를 기반으로 예문 생성, TOEIC/영작 문제 자동 출제, 음성 변환, AI 챗봇 기능을 제공합니다.

## 주요 기능

- 회원가입 / 로그인 (JWT + httpOnly Cookie 인증)
- 단어 저장 및 예문/의미/유의어/반의어 자동 생성
- TOEIC (Part 5/6/7) 및 영작 문제 자동 출제
- Google Cloud TTS 음성 변환
- AI 영어 학습 챗봇
- 단어 및 문제 저장/관리 (MySQL)

## 🛠️ 기술 스택

| 구분 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.5.9 |
| Database | MySQL 8.0 + Spring Data JPA |
| Auth | Spring Security + JWT (httpOnly Cookie) |
| AI | OpenAI GPT API (GPT-4o-mini) |
| TTS | Google Cloud Text-to-Speech |
| Build | Gradle |

## 📁 프로젝트 구조

```
src/main/java/com/example/aienglishtrainer/
├── config/          # 설정 (Security, CORS, OpenAI, TTS)
├── controller/      # API 컨트롤러
├── service/         # 비즈니스 로직
├── repository/      # JPA Repository
├── entity/          # JPA 엔티티
├── dto/             # 요청/응답 DTO
├── security/        # JWT 관련
└── exception/       # 예외 처리
```

## 📚 API 엔드포인트

| 구분 | 엔드포인트 | 설명 |
|------|-----------|------|
| 인증 | `/api/auth` | 회원가입, 로그인, 로그아웃 |
| 단어 | `/api/words` | 단어 CRUD, 검색 |
| 예문 | `/api/examples` | 예문 CRUD, 검색 |
| 문제 | `/api/questions` | TOEIC/영작 문제 CRUD |
| 생성 | `/api/generate` | 예문/문제 AI 생성 |
| TTS | `/api/tts` | 텍스트 음성 변환 |
| 챗봇 | `/api/chat` | AI 챗봇 대화 |

## 🚀 설치 및 실행

다음 프로그램들이 설치되어 있어야 합니다.

- Java 17 이상
- MySQL 8.0 이상

### 1. MySQL 데이터베이스 생성

```sql
CREATE DATABASE english_trainer;
```

### 2. 환경변수 설정

`application-local.yaml.example`을 참고하여 `src/main/resources/application-local.yaml` 파일을 생성합니다.

> ⚠️ `application-local.yaml` 파일은 절대 Git에 커밋하지 마세요.

### 3. Google Cloud TTS 설정 (선택)

1. [Google Cloud Console](https://console.cloud.google.com/)에서 프로젝트 생성
2. Text-to-Speech API 활성화
3. 서비스 계정 생성 및 JSON 키 다운로드
4. 프로젝트 루트에 `config/google-credentials.json` 저장

> TTS 설정을 하지 않아도 Web Speech API로 대체되어 정상 작동합니다.

### 4. 실행

1. IntelliJ IDEA에서 프로젝트 열기
2. Gradle 동기화 완료 대기
3. `AiEnglishTrainerApplication.java` 실행 (▶️ 버튼)

## 저장소

| 구분 | 링크 |
|------|------|
| 백엔드 (Java Spring Boot) | 현재 저장소 |
| 프론트엔드 (React) | https://github.com/HeoSeonJin0504/ai-english-trainer-front |
| 백엔드 (Node.js) | https://github.com/HeoSeonJin0504/ai-english-trainer-node |