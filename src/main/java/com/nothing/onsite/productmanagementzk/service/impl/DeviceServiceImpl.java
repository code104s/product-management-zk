package com.nothing.onsite.productmanagementzk.service.impl;

import com.nothing.onsite.productmanagementzk.model.Device;
import com.nothing.onsite.productmanagementzk.repository.DeviceRepository;
import com.nothing.onsite.productmanagementzk.service.DeviceService;
import com.nothing.onsite.productmanagementzk.utils.IdGenerator;
import com.nothing.onsite.productmanagementzk.viewmodel.DeviceDataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation của DeviceService
 */
@Service
public class DeviceServiceImpl implements DeviceService {
    
    private static final Logger logger = LoggerFactory.getLogger(DeviceServiceImpl.class);
    private static final ConcurrentHashMap<String, Object> deviceLocks = new ConcurrentHashMap<>();
    
    @Autowired
    private DeviceRepository deviceRepository;
    
    @Override
    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }
    
    @Override
    public Optional<Device> getDeviceById(Long id) {
        return deviceRepository.findById(id);
    }
    
    @Override
    public Optional<Device> getDeviceByDeviceId(String deviceId) {
        return deviceRepository.findByDeviceId(deviceId);
    }
    
    @Override
    @Transactional
    public Device saveDevice(Device device) {
        if (device.getId() == null) {
            device.setId(IdGenerator.generateDeviceId());
        }
        if (device.getCreatedAt() == null) {
            device.setCreatedAt(LocalDateTime.now());
        }
        device.setUpdatedAt(LocalDateTime.now());
        return deviceRepository.save(device);
    }
    
    @Override
    @Transactional
    public Device updateDeviceStatus(DeviceDataDto deviceData) {
        // Trước tiên, kiểm tra xem thiết bị đã tồn tại chưa
        Optional<Device> existingDevice = deviceRepository.findByDeviceId(deviceData.deviceId());
        
        if (existingDevice.isPresent()) {
            // Thiết bị đã tồn tại, cập nhật trạng thái
            Device device = existingDevice.get();
            
            if (deviceData.status() != null) {
                device.setStatus(deviceData.status());
            }
            device.setLastOnline(deviceData.timestamp());
            device.setUpdatedAt(LocalDateTime.now());
            
            return deviceRepository.save(device);
        } else {
            // Thiết bị chưa tồn tại, cần tạo mới với cơ chế khóa
            final Object lock = deviceLocks.computeIfAbsent(deviceData.deviceId(), k -> new Object());
            
            synchronized (lock) {
                try {
                    // Kiểm tra lại một lần nữa khi đã có khóa
                    existingDevice = deviceRepository.findByDeviceId(deviceData.deviceId());
                    
                    if (existingDevice.isPresent()) {
                        // Thiết bị đã được tạo bởi tiến trình khác
                        Device device = existingDevice.get();
                        
                        if (deviceData.status() != null) {
                            device.setStatus(deviceData.status());
                        }
                        device.setLastOnline(deviceData.timestamp());
                        device.setUpdatedAt(LocalDateTime.now());
                        
                        return deviceRepository.save(device);
                    } else {
                        // Tạo thiết bị mới
                        logger.info("Tạo thiết bị mới với deviceId: {}", deviceData.deviceId());
                        
                        Device newDevice = new Device();
                        newDevice.setId(IdGenerator.generateDeviceId());
                        newDevice.setDeviceId(deviceData.deviceId());
                        newDevice.setName("Device " + deviceData.deviceId());
                        newDevice.setCreatedAt(LocalDateTime.now());
                        
                        // Set empty values for non-nullable fields in ClickHouse
                        newDevice.setType("");
                        newDevice.setStatus(deviceData.status() != null ? deviceData.status() : "");
                        newDevice.setLocation("");
                        newDevice.setIpAddress("");
                        newDevice.setMacAddress("");
                        newDevice.setFirmwareVersion("");
                        newDevice.setLastOnline(deviceData.timestamp());
                        newDevice.setUpdatedAt(LocalDateTime.now());
                        
                        return deviceRepository.save(newDevice);
                    }
                } finally {
                    // Xóa khóa khi hoàn thành để tránh memory leak
                    deviceLocks.remove(deviceData.deviceId());
                }
            }
        }
    }
    
    @Override
    @Transactional
    public void deleteDevice(Long id) {
        deviceRepository.deleteById(id);
    }
} 