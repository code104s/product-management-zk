package com.nothing.onsite.productmanagementzk.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity lưu trữ cảnh báo thiết bị cho ClickHouse
 */
@Entity
@Table(name = "device_alarms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceAlarm {
    
    @Id
    // Không sử dụng auto generation
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "device_id", nullable = false)
    private String deviceId;
    
    @Column(name = "alarm_type", nullable = false)
    private String alarmType;
    
    @Column(name = "severity", nullable = false)
    private String severity;
    
    @Column(name = "message")
    private String message;
    
    @Column(name = "value")
    private Double value;
    
    @Column(name = "threshold")
    private Double threshold;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "acknowledged")
    private Boolean acknowledged;
    
    @Column(name = "acknowledged_by")
    private String acknowledgedBy;
    
    @Column(name = "acknowledged_at")
    private LocalDateTime acknowledgedAt;
    
    @Column(name = "triggered_at", nullable = false)
    private LocalDateTime triggeredAt;
    
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
} 