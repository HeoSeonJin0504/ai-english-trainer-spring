# AI English Trainer (Backend - Java Spring Boot)
OpenAI GPT API와 Google Cloud TTS를 활용한  
AI 기반 영어 학습 플랫폼의 백엔드 서버입니다.

사용자가 입력한 영어 단어를 기반으로  
예문 생성, TOEIC/영작 문제 자동 출제, 음성 변환 기능을 제공합니다.  
사용자별로 학습 데이터를 관리합니다.

### 주요 기능
- 사용자 회원가입 / 로그인 (JWT 인증)
- 단어 검색, 저장, 삭제 및 예문/의미/유의어/반의어 생성
- TOEIC(Part 5/6/7) 및 영작 문제 자동 출제
- Google TTS 음성 변환
- 데이터베이스를 이용해 단어 및 문제 저장 및 관리

## 🛠️ 기술 스택
- **Language**: Java 17
- **Framework**: Spring Boot 3.5.9
- **Database**: MySQL 8.0 + Spring Data JPA
- **Auth**: Spring Security + JWT
- **AI**: OpenAI GPT API (GPT-4o-mini)
- **TTS**: Google Cloud Text-to-Speech
- **Build**: Gradle

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
| 인증 | `/api/auth` | 회원가입, 로그인 |
| 단어 | `/api/words` | 단어 CRUD, 검색 |
| 예문 | `/api/examples` | 예문 CRUD, 검색 |
| 문제 | `/api/questions` | TOEIC/영작 문제 CRUD |
| 생성 | `/api/generate` | 예문/문제 생성 |
| TTS | `/api/tts` | 텍스트 음성 변환 |

##### 🚀 설치 및 실행
다음 프로그램들이 설치되어 있어야 합니다:
- **Java 17** 이상
- **MySQL 8.0** 이상
- **IntelliJ IDEA**

### 1. MySQL 데이터베이스 생성
```sql
CREATE DATABASE english_trainer;
```

### 2. 환경변수 설정
`application-local.yaml.example` 파일을 참고하여 `application-local.yaml` 파일을 생성합니다.
```bash
cd src/main/resources
cp application-local.yaml.example application-local.yaml
# application-local.yaml.example을 참고하여 application-local.yaml 파일 설정
```

### 3. Google Cloud TTS 설정 (선택)
1. [Google Cloud Console](https://console.cloud.google.com/)에서 프로젝트 생성
2. Text-to-Speech API 활성화
3. 서비스 계정 생성 및 JSON 키 다운로드
4. 프로젝트 루트에 `config/google-credentials.json` 저장

> ⚠️ TTS 설정을 하지 않아도 Web Speech API로 대체되어 정상 작동합니다.

### 4. 실행
**IntelliJ IDEA**
1. 프로젝트 열기 → Gradle 동기화 완료 대기
2. `AiEnglishTrainerApplication.java` 실행 (▶️ 버튼)

**터미널**
```bash
./gradlew bootRun
```

서버가 정상적으로 실행되면 다음과 같은 메시지가 표시됩니다:
```
Started AiEnglishTrainerApplication in X.XXX seconds
```

### 주의사항
- OpenAI API 키는 유료 사용량에 따라 과금됩니다
- Google TTS API는 사용량에 따라 과금됩니다
- `application-local.yaml` 파일은 절대 Git에 커밋하지 마세요

## 저장소
본 프로젝트는 3개의 저장소로 구성되어 있습니다:
- **백엔드 (Java Spring Boot)** - 현재 저장소
    - OpenAI GPT 연동, TTS, 데이터 관리, API 서버
- **프론트엔드 (React)**
    - https://github.com/HeoSeonJin0504/ai-english-trainer-front.git
- **백엔드 (Node.js)** (Java Spring Boot와 기능은 동일)
    - https://github.com/HeoSeonJin0504/ai-english-trainer-node.git