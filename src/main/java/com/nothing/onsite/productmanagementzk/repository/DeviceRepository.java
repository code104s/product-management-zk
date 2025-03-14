package com.nothing.onsite.productmanagementzk.repository;

import com.nothing.onsite.productmanagementzk.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    
    Optional<Device> findByDeviceId(String deviceId);
    
    boolean existsByDeviceId(String deviceId);
} 