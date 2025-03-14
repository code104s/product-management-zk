package com.nothing.onsite.productmanagementzk.controller;

import com.nothing.onsite.productmanagementzk.model.Device;
import com.nothing.onsite.productmanagementzk.service.DeviceService;
import com.nothing.onsite.productmanagementzk.service.KafkaProducerService;
import com.nothing.onsite.productmanagementzk.utils.ApiResponse;
import com.nothing.onsite.productmanagementzk.viewmodel.DeviceDataDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * Controller xử lý dữ liệu thiết bị
 */
@RestController
@RequestMapping("/api/device-data")
public class DeviceDataController {
    
    @Autowired
    private KafkaProducerService kafkaProducerService;
    
    @Autowired
    private DeviceService deviceService;
    
    /**
     * Gửi dữ liệu thiết bị đến Kafka
     */
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<?>> sendDeviceData(@RequestBody DeviceDataDto deviceData) {
        try {
            // Đảm bảo timestamp được thiết lập
            if (deviceData.timestamp() == null) {
                deviceData = new DeviceDataDto(
                        deviceData.deviceId(),
                        deviceData.sensorType(),
                        deviceData.value(),
                        deviceData.unit(),
                        deviceData.status(),
                        LocalDateTime.now()
                );
            }
            
            // Gửi dữ liệu đến Kafka
            kafkaProducerService.sendDeviceData(deviceData);
            
            // Không cần phải gọi updateDeviceStatus() ở đây vì nó sẽ được gọi
            // từ KafkaConsumerService khi xử lý message
            // Device updatedDevice = deviceService.updateDeviceStatus(deviceData);
            
            return new ResponseEntity<>(
                    new ApiResponse<>("SUCCESS", "Dữ liệu thiết bị đã được gửi thành công", deviceData),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ApiResponse<>("ERROR", "Lỗi khi gửi dữ liệu thiết bị: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
} 