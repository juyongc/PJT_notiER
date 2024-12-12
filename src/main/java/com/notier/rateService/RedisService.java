package com.notier.rateService;

import com.notier.entity.AlarmEntity;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisService {

    private static final String ALARM_PREFIX_KEY = "check_send_alarm:";
    private static final String ISSUE_COUPON_KEY = "issue_coupon:";

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

    /**
     * 레디스에 발급할 쿠폰 및 수량 올리는 메서드
     */
    public void issueCouponOnRedis(Long couponId, Long issueAmount) {

        String key = ISSUE_COUPON_KEY + couponId;
        RAtomicLong atomicLong = redissonClient.getAtomicLong(key);

//        if (atomicLong.isExists()) {
//            log.info("기존 쿠폰 수가 발급되어 있습니다. 기존 수: {}, 요청한 값: {}", atomicLong.get(), issueAmount);
//        }

        atomicLong.set(issueAmount);
    }

    /**
     * 레디스에 발급한 쿠폰 및 수량 확인 메서드
     */
    public Long getIssueCouponOnRedis(Long couponId) {

        String key = ISSUE_COUPON_KEY + couponId;
        RAtomicLong atomicLong = redissonClient.getAtomicLong(key);

        return atomicLong.get();
    }

    /**
     * 원자적 연산으로 쿠폰 수 제어 메서드
     */
    public boolean decreaseIssuedCouponAmount(Long couponId) {

        String key = ISSUE_COUPON_KEY + couponId;

        String script = "local amount = redis.call('GET', KEYS[1]) " +
            "if not amount or tonumber(amount) <= 0 then " +
            "   return -1 " +
            "else " +
            "   redis.call('DECR', KEYS[1]) " +
            "   return amount - 1 " +
            "end";

        Long result = redissonClient.getScript().eval(
            RScript.Mode.READ_WRITE,
            script,
            RScript.ReturnType.INTEGER,
            Collections.singletonList(key)
        );

        return result != -1;
    }

    /**
     * redis, mysql 간 트랜잭션 수동 보상 메서드
     */
    public void compensateIssuedCouponAmount(Long couponId) {

        String key = ISSUE_COUPON_KEY + couponId;
        RAtomicLong atomicLong = redissonClient.getAtomicLong(key);
        atomicLong.incrementAndGet();
    }

}
