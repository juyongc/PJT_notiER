package com.notier.repository;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public interface EmitterRepository {

    void save(String emitterId, SseEmitter sseEmitter);

    void saveEventCache(String eventCacheId, Object event);

    Optional<SseEmitter> find(String emitterId);

//    Map<Long, Object> findAllEventCacheByMemberId(Long memberId);
//
//    Map<Long, SseEmitter> findAllEmittersByMemberId(Long memberId);
//
//    void deleteEmitterById(Long id);
//
//    void deleteAllEmitterByMemberId(Long memberId);
//    void deleteAllEventCacheByMemberId(Long memberId);
}
