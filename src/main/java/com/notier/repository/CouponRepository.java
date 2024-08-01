package com.notier.repository;

import com.notier.entity.CouponEntity;
import com.notier.entity.CurrencyEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<CouponEntity, Long> {

    List<CouponEntity> findCouponEntitiesByCurrencyEntity(CurrencyEntity currencyEntity);

}
