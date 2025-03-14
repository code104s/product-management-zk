package com.nothing.onsite.productmanagementzk.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nothing.onsite.productmanagementzk.service.KafkaProducerService;
import com.nothing.onsite.productmanagementzk.viewmodel.DeviceAlarmDto;
import com.nothing.onsite.productmanagementzk.viewmodel.DeviceDataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Implementation của KafkaProducerService
 */
@Service
public class KafkaProducerServiceImpl implements KafkaProducerService {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerServiceImpl.class);
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @Value("${kafka.topic.device-data}")
    private String deviceDataTopic;
    
    @Value("${kafka.topic.device-alarm}")
    private String deviceAlarmTopic;
    
    private final ObjectMapper objectMapper;
    
    public KafkaProducerServiceImpl() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }
    
    @Override
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void sendDeviceData(DeviceDataDto deviceData) {
        try {
            String key = deviceData.deviceId();
            String value = objectMapper.writeValueAsString(deviceData);
            
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(deviceDataTopic, key, value);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("Đã gửi dữ liệu thiết bị đến Kafka: {} với offset: {}", 
                            deviceData.deviceId(), result.getRecordMetadata().offset());
                } else {
                    logger.error("Không thể gửi dữ liệu thiết bị đến Kafka: {}", deviceData.deviceId(), ex);
                }
            });
        } catch (JsonProcessingException e) {
            logger.error("Lỗi chuyển đổi dữ liệu thiết bị sang JSON", e);
            throw new RuntimeException("Lỗi chuyển đổi dữ liệu thiết bị sang JSON", e);
        }
    }
    
    @Override
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void sendDeviceAlarm(DeviceAlarmDto deviceAlarm) {
        try {
            String key = deviceAlarm.deviceId();
            String value = objectMapper.writeValueAsString(deviceAlarm);
            
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(deviceAlarmTopic, key, value);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("Đã gửi cảnh báo thiết bị đến Kafka: {} với offset: {}", 
                            deviceAlarm.deviceId(), result.getRecordMetadata().offset());
                } else {
                    logger.error("Không thể gửi cảnh báo thiết bị đến Kafka: {}", deviceAlarm.deviceId(), ex);
                }
            });
        } catch (JsonProcessingException e) {
            logger.error("Lỗi chuyển đổi cảnh báo thiết bị sang JSON", e);
            throw new RuntimeException("Lỗi chuyển đổi cảnh báo thiết bị sang JSON", e);
        }
    }
} 