package com.nothing.onsite.productmanagementzk.repository;

import com.nothing.onsite.productmanagementzk.model.DeviceData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DeviceDataRepository extends JpaRepository<DeviceData, Long> {
    
    List<DeviceData> findByDeviceIdOrderByTimestampDesc(String deviceId);
    
    @Query(value = "SELECT * FROM device_data WHERE device_id = :deviceId AND timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp", nativeQuery = true)
    List<DeviceData> findDataByTimeRange(@Param("deviceId") String deviceId, 
                                         @Param("startTime") LocalDateTime startTime, 
                                         @Param("endTime") LocalDateTime endTime);
    
    @Query(value = "SELECT * FROM device_data WHERE device_id = :deviceId ORDER BY timestamp DESC LIMIT 1", nativeQuery = true)
    DeviceData findLatestByDeviceId(@Param("deviceId") String deviceId);
} 