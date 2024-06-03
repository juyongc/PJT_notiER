package com.notier.rateService;

import com.notier.dto.ExchangeResponseDto;
import com.notier.entity.CurrencyEntity;
import com.notier.repository.CurrencyRepository;
import java.net.URI;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Transactional
@Slf4j
public class ExchangeService {

    private final RestTemplate restTemplate;
    private final CurrencyRepository currencyRepository;

    @Value("${api.exchange-base-url}")
    private String baseUrl;
    @Value("${api.exchange-authKey}")
    private String authKey;
    @Value("${api.exchange-data}")
    private String data;


    public ExchangeService(RestTemplate restTemplate, CurrencyRepository currencyRepository) {
        this.restTemplate = restTemplate;
        this.currencyRepository = currencyRepository;
    }

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

}
