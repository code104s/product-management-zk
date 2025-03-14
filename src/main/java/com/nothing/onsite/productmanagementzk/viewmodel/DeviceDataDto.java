package com.nothing.onsite.productmanagementzk.viewmodel;

import java.time.LocalDateTime;

/**
 * DTO cho dữ liệu thiết bị từ Kafka
 */
public record DeviceDataDto(
    String deviceId,
    String sensorType,
    Double value,
    String unit,
    String status,
    LocalDateTime timestamp
) {
    // Compact canonical constructor để validate input
    public DeviceDataDto {
        if (deviceId == null || deviceId.isBlank()) {
            throw new IllegalArgumentException("DeviceId không được để trống");
        }
        if (value == null) {
            throw new IllegalArgumentException("Giá trị không được để trống");
        }
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
} 