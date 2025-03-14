package com.nothing.onsite.productmanagementzk.repository;

import com.nothing.onsite.productmanagementzk.model.DeviceAlarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DeviceAlarmRepository extends JpaRepository<DeviceAlarm, Long> {
    
    List<DeviceAlarm> findByDeviceIdOrderByTriggeredAtDesc(String deviceId);
    
    List<DeviceAlarm> findByStatusOrderByTriggeredAtDesc(String status);
    
    @Query(value = "SELECT * FROM device_alarms WHERE device_id = :deviceId AND triggered_at BETWEEN :startTime AND :endTime ORDER BY triggered_at", nativeQuery = true)
    List<DeviceAlarm> findAlarmsByTimeRange(@Param("deviceId") String deviceId, 
                                            @Param("startTime") LocalDateTime startTime, 
                                            @Param("endTime") LocalDateTime endTime);
    
    @Query(value = "SELECT * FROM device_alarms WHERE status = 'ACTIVE' ORDER BY triggered_at DESC", nativeQuery = true)
    List<DeviceAlarm> findActiveAlarms();
} 