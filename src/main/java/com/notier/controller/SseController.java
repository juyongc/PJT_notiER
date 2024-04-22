package com.notier.controller;

import com.notier.rateService.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@RestController
public class SseController {

    private final SseService sseService;

    @GetMapping(value = "/send-alarm", produces = "text/event-stream")
    public SseEmitter sendAlarm() {

        return sseService.subscribe("us");

    }

}
