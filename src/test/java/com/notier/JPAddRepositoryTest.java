package com.notier;

import com.notier.entity.AlarmEntity;
import com.notier.entity.CurrencyEntity;
import com.notier.entity.MemberEntity;
import com.notier.repository.AlarmRepository;
import com.notier.repository.CurrencyRepository;
import com.notier.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class JPAddRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AlarmRepository alarmRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @PersistenceContext
    private EntityManager em;

    @BeforeEach
    void setUp() {

        MemberEntity kim = memberRepository.findMemberEntityByName("kim")
            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다"));

        MemberEntity lee = memberRepository.findMemberEntityByName("lee")
            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다"));

        CurrencyEntity japan = CurrencyEntity.builder()
            .country("jp")
            .exchangeRate(880L)
            .build();
        currencyRepository.save(japan);

        AlarmEntity alarm1 = AlarmEntity.builder()
            .currencyEntity(japan)
            .memberEntity(kim)
            .wishRate(875L)
            .build();
        alarmRepository.save(alarm1);

        AlarmEntity alarm2 = AlarmEntity.builder()
            .currencyEntity(japan)
            .memberEntity(lee)
            .wishRate(900L)
            .build();
        alarmRepository.save(alarm2);

        em.flush();
        em.clear();
    }

    @Test
    void JPRepoTest() {

        List<AlarmEntity> alarmEntities = alarmRepository.findAll();

        alarmEntities
            .forEach(e -> System.out.println(e.getMemberEntity().getName() + "   " + e.getWishRate()));

    }
}
