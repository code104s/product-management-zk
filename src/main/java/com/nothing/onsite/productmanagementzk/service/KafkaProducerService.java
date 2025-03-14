package com.nothing.onsite.productmanagementzk.service;

import com.nothing.onsite.productmanagementzk.viewmodel.DeviceAlarmDto;
import com.nothing.onsite.productmanagementzk.viewmodel.DeviceDataDto;

/**
 * Service gửi dữ liệu đến Kafka
 */
public interface KafkaProducerService {
    
    /**
     * Gửi dữ liệu thiết bị đến Kafka
     */
    void sendDeviceData(DeviceDataDto deviceData);
    
    /**
     * Gửi cảnh báo thiết bị đến Kafka
     */
    void sendDeviceAlarm(DeviceAlarmDto deviceAlarm);
} 