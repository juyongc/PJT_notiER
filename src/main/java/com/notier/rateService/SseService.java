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


    public SseEmitter subscribe(String country) {

        String emitterId = makeEmitterId(country);
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

    public SseEmitter noticeCurrencyToUser(SendAlarmResponseDto sendAlarmResponseDto) {

        String emitterId = makeEmitterId(sendAlarmResponseDto.getCountry());
        SseEmitter emitter = emitterRepositoryImpl.find(emitterId).orElse(null);
        if (emitter == null) {
            return null;
        }

        try {
            log.info("sendAlarmResponseDto = " + sendAlarmResponseDto);
            emitter.send(SseEmitter.event()
                .id(emitterId)
                .name("currency-update : " + sendAlarmResponseDto.getCountry())
                .data(sendAlarmResponseDto)
            );
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
        return emitter;
    }

    private String makeEmitterId(String country) {

        return country + "- Connection";
    }

}
