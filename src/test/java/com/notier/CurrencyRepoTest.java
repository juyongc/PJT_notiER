package com.notier;

import com.notier.repository.CurrencyLogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class CurrencyRepoTest {

    @Autowired
    private CurrencyLogRepository currencyLogRepository;

    @Test
    void currencyLogTest() {
        Long usd = currencyLogRepository.previousExchangeRate("USD");

        System.out.println("usd = " + usd);
    }

}
