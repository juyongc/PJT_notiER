package com.notier.repository;

import com.notier.entity.AlarmLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlarmLogRepository extends JpaRepository<AlarmLogEntity, Long> {

}
