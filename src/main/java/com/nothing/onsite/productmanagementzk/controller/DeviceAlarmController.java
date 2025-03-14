package com.nothing.onsite.productmanagementzk.controller;

import com.nothing.onsite.productmanagementzk.model.DeviceAlarm;
import com.nothing.onsite.productmanagementzk.repository.DeviceAlarmRepository;
import com.nothing.onsite.productmanagementzk.service.KafkaProducerService;
import com.nothing.onsite.productmanagementzk.utils.ApiResponse;
import com.nothing.onsite.productmanagementzk.viewmodel.DeviceAlarmDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller xử lý cảnh báo thiết bị
 */
@RestController
@RequestMapping("/api/device-alarm")
public class DeviceAlarmController {
    
    @Autowired
    private KafkaProducerService kafkaProducerService;
    
    @Autowired
    private DeviceAlarmRepository deviceAlarmRepository;
    
    /**
     * Gửi cảnh báo thiết bị đến Kafka
     */
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<?>> sendDeviceAlarm(@RequestBody DeviceAlarmDto deviceAlarm) {
        try {
            // Đảm bảo timestamp được thiết lập
            if (deviceAlarm.triggeredAt() == null) {
                deviceAlarm = new DeviceAlarmDto(
                        deviceAlarm.deviceId(),
                        deviceAlarm.alarmType(),
                        deviceAlarm.severity(),
                        deviceAlarm.message(),
                        deviceAlarm.value(),
                        deviceAlarm.threshold(),
                        deviceAlarm.status(),
                        LocalDateTime.now()
                );
            }
            
            // Gửi cảnh báo đến Kafka
            kafkaProducerService.sendDeviceAlarm(deviceAlarm);
            
            return new ResponseEntity<>(
                    new ApiResponse<>("SUCCESS", "Cảnh báo thiết bị đã được gửi thành công", deviceAlarm),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ApiResponse<>("ERROR", "Lỗi khi gửi cảnh báo thiết bị: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
    
    /**
     * Lấy danh sách cảnh báo theo thiết bị
     */
    @GetMapping("/device/{deviceId}")
    public ResponseEntity<ApiResponse<?>> getAlarmsByDevice(@PathVariable String deviceId) {
        try {
            List<DeviceAlarm> alarms = deviceAlarmRepository.findByDeviceIdOrderByTriggeredAtDesc(deviceId);
            
            return new ResponseEntity<>(
                    new ApiResponse<>("SUCCESS", "Lấy danh sách cảnh báo thành công", alarms),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ApiResponse<>("ERROR", "Lỗi khi lấy danh sách cảnh báo: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
    
    /**
     * Lấy danh sách cảnh báo đang hoạt động
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<?>> getActiveAlarms() {
        try {
            List<DeviceAlarm> alarms = deviceAlarmRepository.findActiveAlarms();
            
            return new ResponseEntity<>(
                    new ApiResponse<>("SUCCESS", "Lấy danh sách cảnh báo đang hoạt động thành công", alarms),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ApiResponse<>("ERROR", "Lỗi khi lấy danh sách cảnh báo đang hoạt động: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
} 