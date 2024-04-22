package com.notier.repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class EmitterRepositoryImpl implements EmitterRepository {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, Object> eventCache = new ConcurrentHashMap<>();


    @Override
    public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
        emitters.put(emitterId, sseEmitter);
        return sseEmitter;
    }

    @Override
    public void saveEventCache(String eventCacheId, Object event) {
        eventCache.put(eventCacheId, event);
    }

    @Override
    public Optional<SseEmitter> find(String emitterId) {
        return Optional.ofNullable(emitters.get(emitterId));
    }

//    @Override
//    public Map<Long, Object> findAllEventCacheByMemberId(Long memberId) {
//        return eventCache.entrySet().stream()
//            .filter(entry -> entry.getKey().equals(memberId))
//            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//    }
//
//    @Override
//    public Map<Long, SseEmitter> findAllEmittersByMemberId(Long memberId) {
//        return emitters.entrySet().stream()
//            .filter(entry -> entry.getKey().equals(memberId))
//            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//    }
//
//    @Override
//    public void deleteEmitterById(Long id) {
//        emitters.remove(id);
//    }
//
//    @Override
//    public void deleteAllEmitterByMemberId(Long memberId) {
//
//        emitters.forEach(
//            (key,emitter) -> {
//                if (Objects.equals(key, memberId)) {
//                    emitters.remove(key);
//                }
//            }
//        );
//
//    }
//
//    @Override
//    public void deleteAllEventCacheByMemberId(Long memberId) {
//
//        eventCache.forEach(
//            (key, emitter) -> {
//                if (Objects.equals(key,memberId)) {
//                    eventCache.remove(key);
//                }
//            }
//        );
//    }
}
