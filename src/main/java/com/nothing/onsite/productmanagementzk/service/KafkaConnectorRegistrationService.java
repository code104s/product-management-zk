package com.nothing.onsite.productmanagementzk.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Service quản lý đăng ký connector với Kafka Connect
 */
@Service
public class KafkaConnectorRegistrationService {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaConnectorRegistrationService.class);
    
    private final RestTemplate restTemplate;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;
    
    @Value("${kafka.connect.url:http://localhost:8083}")
    private String kafkaConnectUrl;
    
    @Value("${kafka.connect.retry.max-attempts:5}")
    private int maxRetryAttempts;
    
    @Value("${kafka.connect.retry.delay:10000}")
    private long retryDelay;
    
    public KafkaConnectorRegistrationService(RestTemplate restTemplate, ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
    }
    
    /**
     * Đăng ký connector khi ứng dụng khởi động
     */
    @EventListener(ApplicationReadyEvent.class)
    public void registerConnectors() {
        logger.info("Bắt đầu đăng ký connector với Kafka Connect");
        
        // Chờ Kafka Connect khởi động
        waitForKafkaConnect();
        
        // Đăng ký connector cho bộ xử lý device data (processor)
        registerConnector("classpath:kafka-connect/device-data-processor.json");
        
        // Đăng ký connector cho bộ xử lý device alarm (processor)
        registerConnector("classpath:kafka-connect/device-alarm-processor.json");
        
        logger.info("Đã hoàn thành đăng ký connector với Kafka Connect");
    }
    
    /**
     * Đăng ký connector từ file cấu hình
     */
    @Retryable(value = {RestClientException.class, IOException.class}, 
              maxAttempts = 5, 
              backoff = @Backoff(delay = 5000, multiplier = 2))
    private void registerConnector(String configPath) {
        try {
            Resource resource = resourceLoader.getResource(configPath);
            JsonNode connectorConfig = objectMapper.readTree(resource.getInputStream());
            String connectorName = connectorConfig.get("name").asText();
            
            logger.info("Đăng ký connector: {}", connectorName);
            
            // Kiểm tra xem connector đã tồn tại chưa
            if (isConnectorExists(connectorName)) {
                logger.info("Connector {} đã tồn tại. Cập nhật cấu hình...", connectorName);
                updateConnector(connectorName, connectorConfig);
            } else {
                logger.info("Tạo mới connector: {}", connectorName);
                createConnector(connectorConfig);
            }
            
            logger.info("Đăng ký thành công connector: {}", connectorName);
        } catch (Exception e) {
            logger.error("Lỗi khi đăng ký connector từ file: {}", configPath, e);
            throw new RuntimeException("Không thể đăng ký connector", e);
        }
    }
    
    /**
     * Kiểm tra xem connector đã tồn tại chưa
     */
    private boolean isConnectorExists(String connectorName) {
        try {
            String url = kafkaConnectUrl + "/connectors/" + connectorName;
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, createHttpEntity(), String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            logger.debug("Connector {} chưa tồn tại", connectorName);
            return false;
        }
    }
    
    /**
     * Tạo mới connector
     */
    private void createConnector(JsonNode connectorConfig) {
        String url = kafkaConnectUrl + "/connectors";
        restTemplate.exchange(url, HttpMethod.POST, createHttpEntity(connectorConfig), String.class);
    }
    
    /**
     * Cập nhật cấu hình connector
     */
    private void updateConnector(String connectorName, JsonNode connectorConfig) {
        String url = kafkaConnectUrl + "/connectors/" + connectorName + "/config";
        restTemplate.exchange(url, HttpMethod.PUT, createHttpEntity(connectorConfig.get("config")), String.class);
    }
    
    /**
     * Tạo HTTP Entity với JSON
     */
    private HttpEntity<String> createHttpEntity(JsonNode body) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return new HttpEntity<>(objectMapper.writeValueAsString(body), headers);
        } catch (Exception e) {
            logger.error("Lỗi khi tạo HTTP Entity", e);
            throw new RuntimeException("Không thể tạo HTTP request", e);
        }
    }
    
    /**
     * Tạo HTTP Entity rỗng
     */
    private HttpEntity<String> createHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(headers);
    }
    
    /**
     * Chờ Kafka Connect khởi động
     */
    private void waitForKafkaConnect() {
        int attempts = 0;
        boolean isConnected = false;
        
        while (!isConnected && attempts < maxRetryAttempts) {
            try {
                attempts++;
                logger.info("Đang kết nối đến Kafka Connect (lần thử {}): {}", attempts, kafkaConnectUrl);
                
                ResponseEntity<String> response = restTemplate.exchange(
                        kafkaConnectUrl, HttpMethod.GET, createHttpEntity(), String.class);
                
                if (response.getStatusCode().is2xxSuccessful()) {
                    logger.info("Kết nối thành công đến Kafka Connect!");
                    isConnected = true;
                }
            } catch (Exception e) {
                logger.warn("Không thể kết nối đến Kafka Connect. Đang thử lại sau {} ms...", retryDelay);
                try {
                    TimeUnit.MILLISECONDS.sleep(retryDelay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        if (!isConnected) {
            logger.error("Không thể kết nối đến Kafka Connect sau {} lần thử", maxRetryAttempts);
            throw new RuntimeException("Không thể kết nối đến Kafka Connect");
        }
    }
} 