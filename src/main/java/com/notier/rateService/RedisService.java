package com.notier.rateService;

import com.notier.entity.AlarmEntity;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    private static final String ALARM_PREFIX_KEY = "check_send_alarm:";
    private final RedisTemplate<String, Object> objectRedisTemplate;
    private final RedisTemplate<String, String> stringRedisTemplate;

    public RedisService(@Qualifier("valueObjectRedisTemplate") RedisTemplate<String, Object> objectRedisTemplate,
        @Qualifier("valueStringRedisTemplate") RedisTemplate<String, String> stringRedisTemplate) {
        this.objectRedisTemplate = objectRedisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void saveObjectValue(String key, Object value) {
        objectRedisTemplate.opsForValue().set(key, value);
    }

    public Object getObjectValue(String key) {
        return objectRedisTemplate.opsForValue().get(key);
    }

    public void saveStringValue(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    public String getStringValue(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public boolean checkAlreadySendAlarm(AlarmEntity alarmEntity) {

        String key = ALARM_PREFIX_KEY + alarmEntity.getCurrencyEntity().getTicker() + alarmEntity.getMemberEntity()
            .getId();
        Boolean isAlarmed = stringRedisTemplate.hasKey(key);

        return Boolean.TRUE.equals(isAlarmed);
    }

    public void setSendAlarm(AlarmEntity alarmEntity) {
        String key = ALARM_PREFIX_KEY + alarmEntity.getCurrencyEntity().getTicker() + alarmEntity.getMemberEntity()
            .getId();

        Boolean isAlarmed = stringRedisTemplate.hasKey(key);

        if (Boolean.FALSE.equals(isAlarmed)) {
            stringRedisTemplate.opsForValue().set(key, "true");
        }
    }

    public void resetTodayAlarms() {

        Set<String> keys = scanKeys(ALARM_PREFIX_KEY + "*");
        stringRedisTemplate.delete(keys);

    }

    private Set<String> scanKeys(String pattern) {

        Set<String> keys = new HashSet<>();

        ScanOptions options = ScanOptions.scanOptions().match(pattern).count(1000).build();
        Cursor<byte[]> scan = Objects.requireNonNull(stringRedisTemplate.getConnectionFactory()).getConnection()
            .scan(options);

        while (scan.hasNext()) {
            keys.add(new String(scan.next()));
        }

        return keys;
    }
}
