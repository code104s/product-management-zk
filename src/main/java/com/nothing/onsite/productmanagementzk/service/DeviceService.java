package com.nothing.onsite.productmanagementzk.service;

import com.nothing.onsite.productmanagementzk.model.Device;
import com.nothing.onsite.productmanagementzk.viewmodel.DeviceDataDto;

import java.util.List;
import java.util.Optional;

/**
 * Service xử lý thiết bị
 */
public interface DeviceService {
    
    /**
     * Lấy tất cả thiết bị
     */
    List<Device> getAllDevices();
    
    /**
     * Lấy thiết bị theo ID
     */
    Optional<Device> getDeviceById(Long id);
    
    /**
     * Lấy thiết bị theo deviceId
     */
    Optional<Device> getDeviceByDeviceId(String deviceId);
    
    /**
     * Lưu thiết bị
     */
    Device saveDevice(Device device);
    
    /**
     * Cập nhật trạng thái thiết bị từ dữ liệu
     */
    Device updateDeviceStatus(DeviceDataDto deviceData);
    
    /**
     * Xóa thiết bị
     */
    void deleteDevice(Long id);
} 