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
 * Entity lưu trữ dữ liệu thiết bị cho ClickHouse
 */
@Entity
@Table(name = "device_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceData {
    
    @Id
    // Không sử dụng auto generation
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "device_id", nullable = false)
    private String deviceId;
    
    @Column(name = "sensor_type")
    private String sensorType;
    
    @Column(name = "value")
    private Double value;
    
    @Column(name = "unit")
    private String unit;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "timestamp")
    private LocalDateTime timestamp;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
} 