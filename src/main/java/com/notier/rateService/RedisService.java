package com.notier.rateService;

import com.notier.entity.AlarmEntity;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisService {

    private static final String ALARM_PREFIX_KEY = "check_send_alarm:";

    private final RedissonClient redissonClient;

    public void saveObject(String key, Object value) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        bucket.set(value);
    }

    public Object getObject(String key) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

    public void saveString(String key, String value) {
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.set(value);
    }

    public String getString(String key) {
        RBucket<String> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

    public boolean checkAlreadySendAlarm(AlarmEntity alarmEntity) {
        String key =
            ALARM_PREFIX_KEY + alarmEntity.getCurrencyEntity().getTicker() + alarmEntity.getMemberEntity().getId();
        RBucket<String> bucket = redissonClient.getBucket(key);

        return bucket.isExists();
    }

    public void setSendAlarm(AlarmEntity alarmEntity) {
        String key =
            ALARM_PREFIX_KEY + alarmEntity.getCurrencyEntity().getTicker() + alarmEntity.getMemberEntity().getId();
        RBucket<String> bucket = redissonClient.getBucket(key);

        if (!bucket.isExists()) {
            bucket.set("true");
        }
    }

    public void resetTodayAlarms() {
        Set<String> keys = scanKeys(ALARM_PREFIX_KEY + "*");
        redissonClient.getKeys().delete(keys.toArray(new String[0]));
    }

    private Set<String> scanKeys(String pattern) {
        RKeys rKeys = redissonClient.getKeys();
        Iterable<String> keysIterable = rKeys.getKeysByPattern(pattern);
        Set<String> keys = new HashSet<>();

        for (String key : keysIterable) {
            keys.add(key);
        }

        return keys;
    }
}
