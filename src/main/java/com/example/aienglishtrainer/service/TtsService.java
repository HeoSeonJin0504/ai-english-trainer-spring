package com.example.aienglishtrainer.service;

import com.example.aienglishtrainer.dto.tts.TtsRequest;
import com.example.aienglishtrainer.dto.tts.TtsResponse;
import com.example.aienglishtrainer.dto.tts.TtsStatusResponse;
import com.example.aienglishtrainer.exception.BusinessException;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class TtsService {

    private final TextToSpeechClient textToSpeechClient;

    // 텍스트를 음성으로 변환
    public TtsResponse synthesizeSpeech(TtsRequest request) {
        // TTS 클라이언트 확인
        if (textToSpeechClient == null) {
            throw new BusinessException(
                    "TTS 서비스를 사용할 수 없습니다. Web Speech API를 사용해주세요.",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }

        // 음성 선택
        String voiceName = request.getVoice().equals("male")
                ? "en-US-Neural2-D"   // 남성 음성
                : "en-US-Neural2-J";  // 여성 음성

        // 속도 제한 (0.5 ~ 2.0)
        double speed = Math.max(0.5, Math.min(2.0,
                request.getSpeed() != null ? request.getSpeed() : 1.0));

        // 입력 텍스트 설정
        SynthesisInput input = SynthesisInput.newBuilder()
                .setText(request.getText())
                .build();

        // 음성 설정
        VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                .setLanguageCode("en-US")
                .setName(voiceName)
                .build();

        // 오디오 설정
        AudioConfig audioConfig = AudioConfig.newBuilder()
                .setAudioEncoding(AudioEncoding.MP3)
                .setSpeakingRate(speed)
                .setPitch(0)
                .setVolumeGainDb(0)
                .build();

        // TTS 요청
        SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(
                input, voice, audioConfig);

        // 오디오 데이터를 Base64로 인코딩
        ByteString audioContents = response.getAudioContent();
        String audioBase64 = Base64.getEncoder().encodeToString(audioContents.toByteArray());

        log.info("TTS 생성 완료: {}자 -> {} bytes",
                request.getText().length(), audioContents.size());

        return TtsResponse.builder()
                .audio(audioBase64)
                .contentType("audio/mp3")
                .textLength(request.getText().length())
                .build();
    }

    // TTS 서비스 상태 확인
    public TtsStatusResponse checkStatus() {
        boolean available = textToSpeechClient != null;

        return TtsStatusResponse.builder()
                .available(available)
                .message(available
                        ? "✅ Google TTS 사용 가능"
                        : "⚠️ Google TTS 사용 불가 - Web Speech API로 대체 사용")
                .build();
    }
}