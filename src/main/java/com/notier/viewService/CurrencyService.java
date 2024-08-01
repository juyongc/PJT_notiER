package com.notier.viewService;

import com.notier.dto.CurrencyAllResponseDto;
import com.notier.dto.CurrencyHistoryResponseDto;
import com.notier.entity.CouponEntity;
import com.notier.entity.CurrencyEntity;
import com.notier.entity.CurrencyLogEntity;
import com.notier.repository.CouponRepository;
import com.notier.repository.CurrencyLogRepository;
import com.notier.repository.CurrencyRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final CurrencyLogRepository currencyLogRepository;
    private final CouponRepository couponRepository;

    public List<CurrencyEntity> findCurrencyList() {
        List<CurrencyEntity> currencyEntities = currencyRepository.findAll();
        return currencyEntities;
    }

    public List<CurrencyAllResponseDto> combineCurrencyAllResponseList() {

        List<CurrencyEntity> currencyList = findCurrencyList();
        List<CurrencyAllResponseDto> currencyAllResponseDtos = new ArrayList<>();

        for (CurrencyEntity currencyEntity : currencyList) {

            List<CouponEntity> couponEntityList = couponRepository.findCouponEntitiesByCurrencyEntity(
                currencyEntity);

            CurrencyAllResponseDto responseDto = CurrencyAllResponseDto.builder()
                .exchangeRate(currencyEntity.getExchangeRate())
                .ticker(currencyEntity.getTicker())
                .explanation(currencyEntity.getExplanation())
                .couponId(couponEntityList.getFirst().getId())
                .build();

            currencyAllResponseDtos.add(responseDto);
        }

        return currencyAllResponseDtos;
    }

    public Page<CurrencyHistoryResponseDto> findCurrencyHistoryInfoList(String ticker, LocalDateTime aWeekAgo,
        Pageable pageable) {

        Page<CurrencyLogEntity> currencyLogForPagination = currencyLogRepository.findCurrencyLogForPagination(ticker,
            aWeekAgo, pageable);

        return currencyLogForPagination.map(currencyLogEntity ->
            CurrencyHistoryResponseDto.builder()
                .ticker(currencyLogEntity.getTicker())
                .exchangeRate(currencyLogEntity.getExchangeRate())
                .date(currencyLogEntity.getCreatedAt().toLocalDate()).build());
    }
}
