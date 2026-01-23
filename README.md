# AI English Trainer (Java Spring Boot)

AI English Trainer는 OpenAI GPT와 Google Cloud TTS를 활용한 **영어 학습 플랫폼**입니다.

## 📌 프로젝트 개요

사용자가 입력한 영어 단어를 기반으로 AI가 자동으로 예문, 품사, 의미, 유의어/반의어를 생성하고, 
토익(TOEIC) 문제 또는 영작 문제를 자동 출제하는 학습 시스템입니다.
Google Cloud TTS를 통해 생성된 텍스트를 음성으로 들을 수도 있습니다.

### 주요 기능

- **단어 학습**: 단어 추가, 조회, 삭제 및 예문/의미/유의어/반의어 자동 생성
- **단어장**: 단어 및 예문 저장/조회/삭제
- **예문 생성**: OpenAI GPT-4o-mini를 활용한 영어 예문 자동 생성
- **문제 생성**: 토익(Part 5/6/7) 및 영작 문제 자동 출제
- **문제 저장**: 생성된 토익/영작 문제 저장 및 관리
- **음성 변환(TTS)**: Google Cloud TTS를 사용한 텍스트 음성 변환

## 🛠️ 기술 스택

| 구분 | 기술 |
|------|------|
| **Backend** | Java 17, Spring Boot 3.5.9 |
| **Security** | Spring Security, JWT (jjwt 0.12.6) |
| **Database** | MySQL 8.0, Spring Data JPA |
| **AI** | OpenAI API (GPT-4o-mini) |
| **TTS** | Google Cloud Text-to-Speech |
| **Build** | Gradle |

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

## 🔌 API 엔드포인트

### 인증 (Auth)
| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/auth/signup` | 회원가입 |
| POST | `/api/auth/login` | 로그인 (JWT 발급) |
| GET | `/api/auth/check-username` | 아이디 중복 확인 |

### 단어 (Words)
| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/words` | 내 단어 목록 |
| POST | `/api/words` | 단어 저장 |
| DELETE | `/api/words/{id}` | 단어 삭제 |

### 예문 (Examples)
| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/examples` | 내 예문 목록 |
| POST | `/api/examples` | 예문 저장 |
| DELETE | `/api/examples/{id}` | 예문 삭제 |

### 생성 (Generate)
| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/generate/examples` | 단어 예문/의미 생성 |
| POST | `/api/generate/questions` | 토익/영작 문제 생성 |

### 문제 (Questions)
| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/questions` | 내 문제 목록 |
| GET | `/api/questions/toeic` | 토익 문제 목록 |
| GET | `/api/questions/writing` | 영작 문제 목록 |
| POST | `/api/questions` | 문제 저장 |
| DELETE | `/api/questions/{id}` | 문제 삭제 |

### TTS
| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/tts/speak` | 텍스트 → 음성 변환 |
| GET | `/api/tts/status` | TTS 서비스 상태 |

## ⚙️ 환경 설정

다음 프로그램들이 설치되어 있어야 합니다:
- **Java 17** 이상
- **MySQL 8.0** 이상
- **IntelliJ IDEA** 

### 1. application-local.yml 생성

`src/main/resources/application-local.yml`:
```yaml
spring:
  datasource:
  url: jdbc:mysql://localhost:3306/english_trainer?useSSL=false&serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true
  username: root
  password: {비밀번호}
  driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: update

jwt:
  secret: {32자 이상 비밀키}
  expiration: 86400000

openai:
  api-key: {OpenAI API 키}
  model: gpt-4o-mini

google:
  cloud:
    credentials-path: ./config/google-credentials.json
```

### 2. MySQL 데이터베이스 생성
```sql
CREATE DATABASE english_trainer;
```

### 3. Google Cloud TTS 설정

1. [Google Cloud Console](https://console.cloud.google.com/)에서 프로젝트 생성
2. Text-to-Speech API 활성화
3. 서비스 계정 생성 및 JSON 키 다운로드
4. `config/google-credentials.json`에 키 파일 저장
> ⚠️ TTS 설정을 하지 않아도 Web Speech로 정상 작동합니다.

## 🚀 실행 방법

#### 방법 1: IntelliJ IDEA에서 실행
1. **IntelliJ IDEA 실행** → `파일` → `열기` → 프로젝트 폴더 선택
2. Gradle 동기화가 자동으로 진행됩니다.
3. `src/main/java/com/example/aienglishtrainer/AiEnglishTrainerApplication.java` 파일 열기
4. **▶️ 실행 버튼** 클릭

#### 방법 2: 터미널에서 실행
```bash
  gradlew.bat bootRun
```

#### 방법 3: JAR 파일로 실행
```bash
# 빌드
./gradlew build

# 실행
java -jar build/libs/ai-english-trainer-0.0.1-SNAPSHOT.jar
```

브라우저에서 확인:
- http://localhost:8080 → `AI English Trainer API 서버가 정상 작동 중입니다!`

## 📝 주의사항

- `application-local.yml`은 Git에 커밋하지 마세요
- `config/google-credentials.json`은 Git에 커밋하지 마세요

## 저장소
본 프로젝트는 2개의 저장소로 구성되어 있습니다:
- **백엔드 (Java Spring)** - 현재 저장소
    - OpenAI GPT 연동, TTS, 데이터 관리, API 서버
- **프론트엔드 (React)**: https://github.com/HeoSeonJin0504/ai-english-trainer-front.git

## 개발
본 프로젝트는 **GitHub Copilot (Claude Sonnet 4.5)** 및 **Claude Opus 4.5**를 활용하여 코드 작성, 리팩토링 및 문서화 작업을 수행했습니다.