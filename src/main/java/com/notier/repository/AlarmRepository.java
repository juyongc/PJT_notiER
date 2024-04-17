package com.notier.repository;

import com.notier.entity.AlarmEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AlarmRepository extends JpaRepository<AlarmEntity, Long> {

    @Query("select ae from AlarmEntity ae inner join ae.currencyEntity ce where ce.country = :country")
    List<AlarmEntity> findAlarmEntitiesByCurrencyCountry(String country);

}
