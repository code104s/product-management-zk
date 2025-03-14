package com.nothing.onsite.productmanagementzk.viewmodel;

import java.time.LocalDateTime;

/**
 * DTO cho cảnh báo thiết bị từ Kafka
 */
public record DeviceAlarmDto(
    String deviceId,
    String alarmType,
    String severity,
    String message,
    Double value,
    Double threshold,
    String status,
    LocalDateTime triggeredAt
) {
    // Compact canonical constructor để validate input
    public DeviceAlarmDto {
        if (deviceId == null || deviceId.isBlank()) {
            throw new IllegalArgumentException("DeviceId không được để trống");
        }
        if (alarmType == null || alarmType.isBlank()) {
            throw new IllegalArgumentException("Loại cảnh báo không được để trống");
        }
        if (severity == null || severity.isBlank()) {
            throw new IllegalArgumentException("Mức độ nghiêm trọng không được để trống");
        }
        if (triggeredAt == null) {
            triggeredAt = LocalDateTime.now();
        }
    }
} 