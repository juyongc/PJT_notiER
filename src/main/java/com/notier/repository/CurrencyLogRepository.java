package com.notier.repository;

import com.notier.entity.CurrencyLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyLogRepository extends JpaRepository<CurrencyLogEntity, Long> {

    @Query(value = "select cl.exchange_rate from currency_logs cl where cl.ticker = :ticker "
        + "order by cl.created_at desc limit 1 offset 1", nativeQuery = true)
    Long previousExchangeRate(@Param("ticker") String ticker);

}
