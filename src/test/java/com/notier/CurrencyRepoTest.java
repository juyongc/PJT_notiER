package com.notier;

import com.notier.entity.CurrencyEntity;
import com.notier.repository.CurrencyLogRepository;
import com.notier.repository.CurrencyRepository;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class CurrencyRepoTest {

    @Autowired
    private CurrencyLogRepository currencyLogRepository;
    @Autowired
    private CurrencyRepository currencyRepository;

    @Test
    void currencyLogTest() {
        Long usd = currencyLogRepository.previousExchangeRate("USD");

        System.out.println("usd = " + usd);
    }

    @Test
    void setCurrencyRepositoryTest() {

        CurrencyEntity currencyEntity = currencyRepository.findCurrencyEntityByTicker("USD")
            .orElseThrow(() -> new NoSuchElementException("없는 티커입니다"));

        currencyEntity.updateExchangeRate(1350L);
        currencyRepository.save(currencyEntity);
    }

}
