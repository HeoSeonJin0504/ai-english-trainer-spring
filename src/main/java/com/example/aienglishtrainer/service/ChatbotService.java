package com.example.aienglishtrainer.service;

import com.example.aienglishtrainer.dto.chatbot.ChatResponse;
import com.example.aienglishtrainer.dto.chatbot.ConversationHistoryResponse;
import com.example.aienglishtrainer.dto.chatbot.ConversationListResponse;
import com.example.aienglishtrainer.dto.openai.ChatGptRequest;
import com.example.aienglishtrainer.dto.openai.ChatGptResponse;
import com.example.aienglishtrainer.entity.ConversationSession;
import com.example.aienglishtrainer.entity.ConversationSession.ChatMessage;
import com.example.aienglishtrainer.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotService {

    private final WebClient openAiWebClient;

    @Value("${openai.model}")
    private String model;

    // 대화 히스토리 저장 (메모리 기반 - Thread-safe)
    private final Map<String, ConversationSession> conversationHistory = new ConcurrentHashMap<>();

    // 대화 세션 최대 유지 시간 (30분)
    private static final int SESSION_TIMEOUT_MINUTES = 30;

    /**
     * 사용자 메시지에 응답
     */
    public ChatResponse sendMessage(String userId, String message, String conversationId) {
        // 대화 세션 ID 생성 또는 기존 세션 사용
        String sessionId = (conversationId != null && !conversationId.isBlank())
                ? conversationId
                : generateSessionId(userId);

        // 대화 히스토리 가져오기 또는 생성
        ConversationSession session = getOrCreateSession(sessionId, userId);

        // 사용자 메시지 추가
        session.addMessage("user", message);

        // 최근 10개 메시지만 사용 (토큰 절약)
        List<ChatMessage> recentMessages = session.getRecentMessages(10);

        try {
            // OpenAI API 호출
            List<ChatGptRequest.Message> messages = new ArrayList<>();

            // 시스템 프롬프트 추가
            messages.add(ChatGptRequest.Message.builder()
                    .role("system")
                    .content(getSystemPrompt())
                    .build());

            // 최근 대화 히스토리 추가
            for (ChatMessage msg : recentMessages) {
                messages.add(ChatGptRequest.Message.builder()
                        .role(msg.getRole())
                        .content(msg.getContent())
                        .build());
            }

            ChatGptRequest request = ChatGptRequest.builder()
                    .model(model)
                    .messages(messages)
                    .max_tokens(500)
                    .temperature(0.8)
                    .build();

            ChatGptResponse response = openAiWebClient.post()
                    .uri("/chat/completions")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ChatGptResponse.class)
                    .block();

            if (response == null || response.getContent() == null) {
                throw new BusinessException("챗봇 응답을 받을 수 없습니다.", HttpStatus.SERVICE_UNAVAILABLE);
            }

            String aiResponse = response.getContent();

            // AI 응답 히스토리에 추가
            session.addMessage("assistant", aiResponse);

            // 추천 질문 생성
            List<String> suggestions = generateSuggestions(message, aiResponse);

            log.info("챗봇 응답 생성 완료 - 세션: {}, 메시지 수: {}", sessionId, session.getMessages().size());

            return ChatResponse.builder()
                    .message(aiResponse)
                    .conversationId(sessionId)
                    .suggestions(suggestions)
                    .timestamp(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("ChatGPT API 호출 실패: {}", e.getMessage());
            throw new BusinessException("챗봇 응답 생성에 실패했습니다: " + e.getMessage(),
                    HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * 대화 히스토리 조회
     */
    public ConversationHistoryResponse getConversationHistory(String conversationId) {
        ConversationSession session = conversationHistory.get(conversationId);
        if (session == null) {
            throw new BusinessException("대화를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }

        List<ConversationHistoryResponse.MessageDto> messages = session.getMessages().stream()
                .map(msg -> ConversationHistoryResponse.MessageDto.builder()
                        .role(msg.getRole())
                        .content(msg.getContent())
                        .build())
                .collect(Collectors.toList());

        return ConversationHistoryResponse.builder()
                .conversationId(conversationId)
                .messages(messages)
                .startedAt(session.getStartedAt())
                .lastActivity(session.getLastActivity())
                .build();
    }

    /**
     * 대화 삭제
     */
    public void deleteConversation(String conversationId) {
        ConversationSession removed = conversationHistory.remove(conversationId);
        if (removed == null) {
            throw new BusinessException("대화를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }
        log.info("대화 삭제 완료: {}", conversationId);
    }

    /**
     * 사용자의 모든 대화 목록 가져오기
     */
    public List<ConversationListResponse> getUserConversations(String userId) {
        return conversationHistory.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(userId + "_"))
                .map(entry -> {
                    String sessionId = entry.getKey();
                    ConversationSession session = entry.getValue();

                    // 첫 번째 메시지 미리보기
                    String preview = session.getMessages().isEmpty()
                            ? "대화 없음"
                            : session.getMessages().get(0).getContent();

                    if (preview.length() > 50) {
                        preview = preview.substring(0, 50) + "...";
                    }

                    return ConversationListResponse.builder()
                            .conversationId(sessionId)
                            .preview(preview)
                            .messageCount(session.getMessages().size())
                            .startedAt(session.getStartedAt())
                            .lastActivity(session.getLastActivity())
                            .build();
                })
                .sorted(Comparator.comparing(ConversationListResponse::getLastActivity).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 대화 세션 ID 생성
     */
    private String generateSessionId(String userId) {
        return String.format("%s_%d_%s",
                userId,
                System.currentTimeMillis(),
                UUID.randomUUID().toString().substring(0, 9));
    }

    /**
     * 대화 세션 가져오기 또는 생성
     */
    private ConversationSession getOrCreateSession(String sessionId, String userId) {
        return conversationHistory.computeIfAbsent(sessionId,
                id -> ConversationSession.createNew(id, userId));
    }

    /**
     * 추천 질문 생성 (간단한 로직)
     */
    private List<String> generateSuggestions(String userMessage, String aiResponse) {
        // 간단한 추천 질문 템플릿
        List<String> suggestions = Arrays.asList(
                "Can you explain that in simpler terms?",
                "What's another way to say that?",
                "Can you give me an example?"
        );

        return suggestions.subList(0, 2); // 2개만 반환
    }

    /**
     * 오래된 대화 세션 정리 (10분마다 실행)
     */
    @Scheduled(fixedRate = 10 * 60 * 1000) // 10분
    public void cleanupOldSessions() {
        LocalDateTime now = LocalDateTime.now();
        int removedCount = 0;

        Iterator<Map.Entry<String, ConversationSession>> iterator =
                conversationHistory.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, ConversationSession> entry = iterator.next();
            ConversationSession session = entry.getValue();

            long minutesSinceLastActivity = ChronoUnit.MINUTES.between(
                    session.getLastActivity(), now);

            if (minutesSinceLastActivity > SESSION_TIMEOUT_MINUTES) {
                iterator.remove();
                removedCount++;
                log.info("세션 만료: {}", entry.getKey());
            }
        }

        if (removedCount > 0) {
            log.info("세션 정리 완료: {}개 삭제, 남은 세션: {}개",
                    removedCount, conversationHistory.size());
        }
    }

    /**
     * 시스템 프롬프트 (챗봇 역할 정의)
     */
    private String getSystemPrompt() {
        return """
You are "English Buddy", a friendly AI English tutor specifically designed for Korean learners.

## Your Primary Purpose
Help Korean users improve their English through conversation practice, explanations, and guidance.

## Language Response Rules (CRITICAL)

1. **Language Detection & Matching:**
   - If user writes in ENGLISH → Respond in ENGLISH
   - If user writes in KOREAN → Respond in KOREAN
   - If user writes in MIXED (Korean + English) → Respond in the DOMINANT language
   - ALWAYS match the user's language choice

2. **Examples:**
   - User: "How do you say '안녕' in English?" → English response
   - User: "이 문장 맞아? I go to school yesterday" → Korean response (with explanation)
   - User: "반가워요! 영어 공부 도와줘" → Korean response

## ✅ Appropriate Topics (Answer These)

**English Learning:**
- Grammar explanations (문법 설명)
- Vocabulary and word meanings (어휘와 단어 뜻)
- Pronunciation tips (발음 팁)
- Writing corrections (글쓰기 교정)
- Sentence structure (문장 구조)
- Idioms and expressions (관용구와 표현)
- TOEIC/TOEFL/IELTS prep (시험 준비)

**Practice Conversations:**
- Daily situations (일상 대화)
- Travel English (여행 영어)
- Business English (비즈니스 영어)
- Job interviews (면접)
- Small talk (스몰톡)

**Cultural Topics (Related to Language Learning):**
- English-speaking countries' cultures
- Language learning tips
- Study methods

## ❌ Off-Topic Requests (Politely Decline)

If user asks about topics UNRELATED to English learning, respond with:

**In English (if they used English):**
"I appreciate your question, but I'm specifically designed to help with English learning. I'd be happy to discuss topics like grammar, vocabulary, conversation practice, or English culture instead! What aspect of English would you like to practice today?"

**In Korean (if they used Korean):**
"질문 감사합니다만, 저는 영어 학습을 도와드리기 위해 만들어진 AI입니다. 문법, 어휘, 회화 연습, 영어권 문화 등 영어와 관련된 주제라면 기꺼이 도와드릴게요! 어떤 영어 학습을 원하시나요?"

**Off-topic examples to decline:**
- Math problems (unless explaining how to discuss them in English)
- Medical advice
- Legal advice
- Programming code (unless teaching English technical vocabulary)
- Personal life counseling
- Politics/religion debates
- Breaking news/current events (unless as conversation practice)
- Homework in other subjects (역사, 수학, 과학 숙제 등)

## Response Style

**When user practices English:**
- Respond naturally in English
- If they make mistakes:
  1. First, acknowledge their message positively
  2. Then gently correct: "Great question! Just a small tip: we say 'I *went* to school yesterday' (past tense)."
- Keep responses 2-4 sentences (unless detailed explanation requested)
- Ask follow-up questions to continue practice

**When user asks in Korean:**
- Give clear, detailed Korean explanations
- Include English examples with Korean translations
- Example format:\s
  "went"는 "go"의 과거형입니다.
  예문: "I went to school yesterday." (나는 어제 학교에 갔다.)

**Grammar Corrections:**
- Be encouraging, not harsh
- Use this format: "You're doing great! Small note: [correction]"
- Explain WHY (especially in Korean if they ask in Korean)

## Tone
- Warm and encouraging
- Patient and supportive
- Never condescending
- Celebrate small wins
- Match user's energy (formal ↔ casual)

## Special Situations

**If user seems frustrated:**
- Encourage them in their language
- Suggest easier practice methods
- Remind them learning takes time

**If question is unclear:**
- Ask for clarification politely
- In English: "Could you clarify what you mean?"
- In Korean: "조금 더 자세히 설명해주시겠어요?"

**If user switches languages mid-conversation:**
- Smoothly switch to match their new language
- Don't comment on the switch, just adapt

## Quick Rules
- Response length: 2-4 sentences (unless detailed explanation needed)
- Always be respectful and professional
- If unsure about a grammar rule, say so honestly
- Focus on practical, usable English
- Encourage daily practice

Remember: You are NOT a general knowledge AI. You are an ENGLISH TUTOR. Stay focused on helping users improve their English skills!
""";
    }
}