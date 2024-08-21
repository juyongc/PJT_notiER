package com.notier.backOffice;

import com.notier.entity.CurrencyEntity;
import com.notier.repository.CurrencyRepository;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


@Service
@Transactional
@Slf4j
public class BackofficeService {

    private final RestTemplate restTemplate;
    private final CurrencyRepository currencyRepository;

    @Value("${api.exchange-base-url}")
    private String baseUrl;
    @Value("${api.exchange-authKey}")
    private String authKey;
    @Value("${api.exchange-data}")
    private String data;
    private final Random random = new Random();


    public BackofficeService(RestTemplate restTemplate, CurrencyRepository currencyRepository) {
        this.restTemplate = restTemplate;
        this.currencyRepository = currencyRepository;
    }

    /**
     * 실제 환율 연동시, 사용할 서비스
     */
    public String callExchangeOpenApi() {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
            .path("/site/program/financial/exchangeJSON")
            .queryParam("authkey", authKey)
            .queryParam("data", data)
            .build()
            .toUri();

        ExchangeResponseDto[] erDtoList = restTemplate.getForObject(uri, ExchangeResponseDto[].class);
        if (erDtoList != null) {
            Arrays.stream(erDtoList)
                .map(erDto -> CurrencyEntity.builder()
                    .ticker(erDto.getTicker())
                    .explanation(erDto.getExplanation())
                    .exchangeRate(erDto.getExchangeRate())
                    .build())
                .forEach(currencyRepository::save);
        }

        return restTemplate.getForObject(uri, String.class);
    }

    /**
     * 전체 환율 변동 메서드
     */
    public List<BackofficeCurrencyResponseDto> modifyAllCurrentCurrency() {

        List<CurrencyEntity> currencyEntities = currencyRepository.findAll();

        List<BackofficeCurrencyResponseDto> responseDtoList = new ArrayList<>();
        currencyEntities.stream()
            .filter(ce -> !ce.getTicker().equals("KRW"))
            .forEach(ce -> {
                Long currencyRate = ce.getExchangeRate() + random.nextInt(31) - 5;
                responseDtoList.add(
                    BackofficeCurrencyResponseDto.builder()
                        .ticker(ce.getTicker())
                        .explanation(ce.getExplanation())
                        .exchangeRate(currencyRate).build());
            });

        return responseDtoList;
    }
}
