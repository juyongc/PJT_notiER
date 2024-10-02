package com.notier.rateService;

import com.notier.dto.SendAlarmResponseDto;
import com.notier.repository.EmitterRepository;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@Slf4j
@Service
public class SseService {

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final EmitterRepository emitterRepositoryImpl;


    public SseEmitter subscribe(String ticker) {

        String emitterId = makeEmitterId(ticker);
        SseEmitter emitter = emitterRepositoryImpl.find(emitterId)
            .orElseGet(() -> {
                SseEmitter newEmitter = new SseEmitter(DEFAULT_TIMEOUT);
                emitterRepositoryImpl.save(emitterId, newEmitter);
                return newEmitter;
            });

        try {
            emitter.send(SseEmitter.event()
                .id(emitterId)
                .name("connection check")
                .data("connected")
            );
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        return emitter;
    }

    public void noticeCurrencyToUser(SendAlarmResponseDto sendAlarmResponseDto) {

        if (sendAlarmResponseDto == null) {
            return;
        }

        String emitterId = makeEmitterId(sendAlarmResponseDto.getTicker());
        SseEmitter emitter = emitterRepositoryImpl.find(emitterId).orElse(null);
        if (emitter == null) {
            return;
        }

        try {
            emitter.send(SseEmitter.event()
                .id(emitterId)
                .name("currency-update : " + sendAlarmResponseDto.getTicker())
                .data(sendAlarmResponseDto)
            );
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
    }

    private String makeEmitterId(String ticker) {

        return ticker + "- Connection";
    }

}
