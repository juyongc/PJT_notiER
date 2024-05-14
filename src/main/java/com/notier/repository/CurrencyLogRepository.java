package com.notier.repository;

import com.notier.entity.CurrencyLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyLogRepository extends JpaRepository<CurrencyLogEntity, Long> {

}
