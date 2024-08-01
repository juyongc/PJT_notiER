package com.notier.repository;

import com.notier.entity.CurrencyLogEntity;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyLogRepository extends JpaRepository<CurrencyLogEntity, Long> {

    @Query(value = "select cl.exchange_rate from currency_logs cl where cl.ticker = :ticker "
        + "order by cl.created_at desc limit 1 offset 1", nativeQuery = true)
    Long previousExchangeRate(@Param("ticker") String ticker);

    @Query("select cle from CurrencyLogEntity cle where cle.ticker = :ticker and cle.createdAt > :fromDate order by cle.createdAt desc")
    Page<CurrencyLogEntity> findCurrencyLogForPagination(@Param("ticker") String ticker,
        @Param("fromDate") LocalDateTime fromDate, Pageable pageable);



}
