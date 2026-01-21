package com.example.aienglishtrainer.service;

import com.example.aienglishtrainer.dto.openai.*;
import com.example.aienglishtrainer.exception.BusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAiService {

    private final WebClient openAiWebClient;
    private final ObjectMapper objectMapper;

    @Value("${openai.model}")
    private String model;

    // 예문 생성
    public GenerateExampleResponse generateExamples(String word) {
        String prompt = buildExamplePrompt(word);

        ChatGptRequest request = ChatGptRequest.builder()
                .model(model)
                .messages(List.of(
                        ChatGptRequest.Message.builder()
                                .role("system")
                                .content("당신은 영어 교육 전문가입니다. 학습자가 단어를 깊이 이해할 수 있도록 상세한 정보를 제공해주세요.")
                                .build(),
                        ChatGptRequest.Message.builder()
                                .role("user")
                                .content(prompt)
                                .build()
                ))
                .max_tokens(800)
                .temperature(0.7)
                .build();

        String responseContent = callChatGpt(request);
        return parseExampleResponse(responseContent, word);
    }

    // 문제 생성
    public GenerateQuestionResponse generateQuestions(String topic, String mode) {
        String prompt = mode.equals("toeic")
                ? buildToeicPrompt(topic)
                : buildWritingPrompt(topic);

        ChatGptRequest request = ChatGptRequest.builder()
                .model(model)
                .messages(List.of(
                        ChatGptRequest.Message.builder()
                                .role("system")
                                .content("당신은 영어 시험 문제를 출제하는 전문가입니다.")
                                .build(),
                        ChatGptRequest.Message.builder()
                                .role("user")
                                .content(prompt)
                                .build()
                ))
                .max_tokens(950)
                .temperature(0.7)
                .build();

        String responseContent = callChatGpt(request);
        return parseQuestionResponse(responseContent, mode);
    }

    // ChatGPT API 호출
    private String callChatGpt(ChatGptRequest request) {
        try {
            ChatGptResponse response = openAiWebClient.post()
                    .uri("/chat/completions")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ChatGptResponse.class)
                    .block();

            if (response == null || response.getContent() == null) {
                throw new BusinessException("ChatGPT 응답이 없습니다.", HttpStatus.SERVICE_UNAVAILABLE);
            }

            log.info("ChatGPT 응답: {}", response.getContent());
            return response.getContent();

        } catch (Exception e) {
            log.error("ChatGPT API 호출 실패: {}", e.getMessage());
            throw new BusinessException("ChatGPT API 호출에 실패했습니다: " + e.getMessage(),
                    HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    // JSON 추출 (응답에서 JSON 부분만 추출)
    private String extractJson(String content) {
        Pattern pattern = Pattern.compile("\\{[\\s\\S]*\\}");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group();
        }
        throw new BusinessException("JSON 형식 응답을 찾을 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 예문 응답 파싱
    private GenerateExampleResponse parseExampleResponse(String content, String word) {
        try {
            String json = extractJson(content);
            JsonNode root = objectMapper.readTree(json);

            // 유효하지 않은 단어인 경우
            if (root.has("isValid") && !root.get("isValid").asBoolean()) {
                return GenerateExampleResponse.builder()
                        .isValid(false)
                        .errorMessage(root.has("errorMessage")
                                ? root.get("errorMessage").asText()
                                : "유효한 영어 단어가 아닙니다.")
                        .build();
            }

            return objectMapper.readValue(json, GenerateExampleResponse.class);

        } catch (JsonProcessingException e) {
            log.error("예문 응답 파싱 실패: {}", e.getMessage());
            throw new BusinessException("응답 파싱에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 문제 응답 파싱
    private GenerateQuestionResponse parseQuestionResponse(String content, String mode) {
        try {
            String json = extractJson(content);
            JsonNode root = objectMapper.readTree(json);

            if (mode.equals("toeic")) {
                return parseToeicResponse(root);
            } else {
                return parseWritingResponse(root);
            }

        } catch (JsonProcessingException e) {
            log.error("문제 응답 파싱 실패: {}", e.getMessage());
            throw new BusinessException("응답 파싱에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private GenerateQuestionResponse parseToeicResponse(JsonNode root) throws JsonProcessingException {
        JsonNode questionsNode = root.get("questions");

        GenerateQuestionResponse.ToeicQuestions questions = objectMapper.treeToValue(
                questionsNode, GenerateQuestionResponse.ToeicQuestions.class);

        return GenerateQuestionResponse.builder()
                .mode("toeic")
                .questions(questions)
                .build();
    }

    private GenerateQuestionResponse parseWritingResponse(JsonNode root) throws JsonProcessingException {
        JsonNode questionsNode = root.get("questions");

        List<GenerateQuestionResponse.WritingQuestion> questions = objectMapper.readValue(
                questionsNode.toString(),
                objectMapper.getTypeFactory().constructCollectionType(
                        List.class, GenerateQuestionResponse.WritingQuestion.class));

        return GenerateQuestionResponse.builder()
                .mode("writing")
                .writingQuestions(questions)
                .build();
    }

    // 예문 생성 프롬프트
    private String buildExamplePrompt(String word) {
        return String.format("""
            "%s"가 유효한 영어 단어인지 먼저 확인하고, 학습 자료를 JSON 형식으로 만들어주세요.

            ⚠️ 반드시 아래 JSON 형식만 출력하세요. 다른 텍스트는 포함하지 마세요.

            유효하지 않은 단어인 경우:
            {
              "isValid": false,
              "errorMessage": "유효한 영어 단어가 아닙니다"
            }

            유효한 단어인 경우:
            {
              "isValid": true,
              "word": {
                "original": "%s",
                "meanings": [
                  {
                    "partOfSpeech": "품사 (예: 명사, 동사, 형용사)",
                    "meaning": "한국어 뜻"
                  }
                ]
              },
              "examples": [
                {
                  "english": "영어 예문 (단어를 포함한 자연스러운 문장)",
                  "korean": "한국어 번역"
                }
              ],
              "relatedWords": {
                "synonym": {
                  "word": "유의어",
                  "partOfSpeech": "품사",
                  "meaning": "한국어 뜻"
                },
                "antonym": {
                  "word": "반의어",
                  "partOfSpeech": "품사",
                  "meaning": "한국어 뜻"
                }
              }
            }

            📌 중요 규칙:
            - meanings는 1~3개 제공
            - examples는 정확히 3개 제공
            - examples는 각각 초급, 중급, 고급 수준으로 작성
            - synonym(유의어)는 반드시 제공
            - antonym(반의어)는 가능한 한 제공하되, 정말로 적절한 반의어가 없으면 null
            - JSON만 출력하고 다른 설명은 절대 포함하지 마세요
            """, word, word);
    }

    // 토익 문제 생성 프롬프트
    private String buildToeicPrompt(String topic) {
        return String.format("""
            당신은 토익(TOEIC) 문제를 전문적으로 출제하는 AI입니다.
            반드시 아래 형식을 지키고, JSON만 출력하세요.

            총 6개의 문제를 아래와 같이 출제하세요:

            📌 Part 5 - 문법 빈칸 문제(2문항)
            📌 Part 6 - 문장 삽입 문제(2문항)
            📌 Part 7 - 독해 문제(2문항)

            ⚠️ 반드시 JSON ONLY로 출력하세요.

            {
              "mode": "toeic",
              "questions": {
                "part5": [
                  {
                    "question": "",
                    "options": { "A": "", "B": "", "C": "", "D": "" },
                    "answer": "",
                    "explanation": ""
                  }
                ],
                "part6": [
                  {
                    "passage": "",
                    "insertSentence": "",
                    "question": "Where should the sentence be inserted?",
                    "options": { "A": "[1]", "B": "[2]", "C": "[3]", "D": "[4]" },
                    "answer": "",
                    "explanation": ""
                  }
                ],
                "part7": [
                  {
                    "passage": "",
                    "question": "",
                    "options": { "A": "", "B": "", "C": "", "D": "" },
                    "answer": "",
                    "explanation": ""
                  }
                ]
              }
            }

            "%s"을 반영하여 자연스럽게 출제하세요.
            해설(explanation)은 반드시 한국어로 작성하세요.
            출력은 반드시 JSON만 포함해야 하며, 그 외의 텍스트는 절대 포함하지 마세요.
            """, topic);
    }

    // 영작 문제 생성 프롬프트
    private String buildWritingPrompt(String topic) {
        return String.format("""
            당신은 영어 학습자를 위한 영작 연습 문제를 만드는 전문가입니다.
            "%s"을 기반으로 총 4개의 영작 문제를 만들어주세요.

            문제 유형(랜덤 배정):
            1) 상황 설명 기반 영작
            2) 한→영 번역
            3) 문장 자연스럽게 고치기
            4) 짧은 답변식 영작

            ⚠️ JSON ONLY로 출력하세요:

            {
              "mode": "writing",
              "questions": [
                {
                  "type": "situation | translation | fix | short-answer",
                  "question": "문제 설명 (한국어)",
                  "hint": "필요하면 간단한 힌트",
                  "answer": "모범답안(영어)"
                }
              ]
            }
            JSON 이외의 텍스트는 절대 포함하지 마세요.
            """, topic);
    }
}