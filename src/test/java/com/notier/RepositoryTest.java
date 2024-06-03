package com.notier;

import com.notier.entity.AlarmEntity;
import com.notier.entity.CurrencyEntity;
import com.notier.entity.MemberEntity;
import com.notier.repository.AlarmRepository;
import com.notier.repository.CurrencyRepository;
import com.notier.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.NoSuchElementException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
//@TestPropertySource(locations = "classpath:application-test.yml")
//@ActiveProfiles("test")
class RepositoryTest {

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

        MemberEntity kim = MemberEntity.builder()
            .name("kim")
            .build();
        memberRepository.save(kim);

        MemberEntity lee = MemberEntity.builder()
            .name("lee")
            .build();
        memberRepository.save(lee);

        CurrencyEntity us = currencyRepository.findCurrencyEntityByTicker("USD")
            .orElseThrow(() -> new NoSuchElementException("없는 티커입니다"));

        AlarmEntity alarm1 = AlarmEntity.builder()
            .currencyEntity(us)
            .memberEntity(kim)
            .wishRate(1340L)
            .build();
        alarmRepository.save(alarm1);

        AlarmEntity alarm2 = AlarmEntity.builder()
            .currencyEntity(us)
            .memberEntity(lee)
            .wishRate(1330L)
            .build();
        alarmRepository.save(alarm2);

        em.flush();
        em.clear();
    }

    @Test
    void RepoTest() {

        MemberEntity memberEntity = memberRepository.findMemberEntityByName("kim")
            .orElseThrow(() -> new NoSuchElementException("No member"));

        Assertions.assertThat(memberEntity.getName()).isEqualTo("kim");

    }
}
