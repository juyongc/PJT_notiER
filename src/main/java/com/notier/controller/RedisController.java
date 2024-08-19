package com.notier.controller;

import com.notier.rateService.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/redis")
public class RedisController {

    private final RedisService redisService;

    @PostMapping("/save")
    public String saveData(@RequestParam String key, @RequestParam Object value) {
        redisService.saveObject(key, value);
        return "Save Success~~~!!";
    }

    @GetMapping("/get")
    public Object getData(@RequestParam String key) {
        return redisService.getObject(key);
    }


}
