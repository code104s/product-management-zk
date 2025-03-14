package com.nothing.onsite.productmanagementzk.controller;

import com.nothing.onsite.productmanagementzk.service.KafkaConnectorRegistrationService;
import com.nothing.onsite.productmanagementzk.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Controller để quản lý Kafka Connect
 */
@RestController
@RequestMapping("/api/kafka-connect")
public class KafkaConnectController {
    
    private final RestTemplate restTemplate;
    private final KafkaConnectorRegistrationService registrationService;
    
    @Value("${kafka.connect.url:http://localhost:8083}")
    private String kafkaConnectUrl;
    
    public KafkaConnectController(RestTemplate restTemplate, KafkaConnectorRegistrationService registrationService) {
        this.restTemplate = restTemplate;
        this.registrationService = registrationService;
    }
    
    /**
     * Lấy danh sách tất cả connectors
     */
    @GetMapping("/connectors")
    public ResponseEntity<ApiResponse<?>> getAllConnectors() {
        try {
            String url = kafkaConnectUrl + "/connectors";
            ResponseEntity<Object> response = restTemplate.exchange(
                    url, HttpMethod.GET, createHttpEntity(), Object.class);
            
            return new ResponseEntity<>(
                    new ApiResponse<>("SUCCESS", "Lấy danh sách connectors thành công", response.getBody()),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ApiResponse<>("ERROR", "Lỗi khi lấy danh sách connectors: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
    
    /**
     * Lấy thông tin chi tiết của connector
     */
    @GetMapping("/connectors/{name}")
    public ResponseEntity<ApiResponse<?>> getConnector(@PathVariable String name) {
        try {
            String url = kafkaConnectUrl + "/connectors/" + name;
            ResponseEntity<Object> response = restTemplate.exchange(
                    url, HttpMethod.GET, createHttpEntity(), Object.class);
            
            return new ResponseEntity<>(
                    new ApiResponse<>("SUCCESS", "Lấy thông tin connector thành công", response.getBody()),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ApiResponse<>("ERROR", "Lỗi khi lấy thông tin connector: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
    
    /**
     * Lấy status của connector
     */
    @GetMapping("/connectors/{name}/status")
    public ResponseEntity<ApiResponse<?>> getConnectorStatus(@PathVariable String name) {
        try {
            String url = kafkaConnectUrl + "/connectors/" + name + "/status";
            ResponseEntity<Object> response = restTemplate.exchange(
                    url, HttpMethod.GET, createHttpEntity(), Object.class);
            
            return new ResponseEntity<>(
                    new ApiResponse<>("SUCCESS", "Lấy status connector thành công", response.getBody()),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ApiResponse<>("ERROR", "Lỗi khi lấy status connector: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
    
    /**
     * Khởi động lại connector
     */
    @GetMapping("/connectors/{name}/restart")
    public ResponseEntity<ApiResponse<?>> restartConnector(@PathVariable String name) {
        try {
            String url = kafkaConnectUrl + "/connectors/" + name + "/restart";
            restTemplate.exchange(url, HttpMethod.POST, createHttpEntity(), Object.class);
            
            return new ResponseEntity<>(
                    new ApiResponse<>("SUCCESS", "Khởi động lại connector thành công", null),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ApiResponse<>("ERROR", "Lỗi khi khởi động lại connector: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
    
    /**
     * Xóa connector
     */
    @DeleteMapping("/connectors/{name}")
    public ResponseEntity<ApiResponse<?>> deleteConnector(@PathVariable String name) {
        try {
            String url = kafkaConnectUrl + "/connectors/" + name;
            restTemplate.exchange(url, HttpMethod.DELETE, createHttpEntity(), Object.class);
            
            return new ResponseEntity<>(
                    new ApiResponse<>("SUCCESS", "Xóa connector thành công", null),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ApiResponse<>("ERROR", "Lỗi khi xóa connector: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
    
    /**
     * Đăng ký lại tất cả connector
     */
    @GetMapping("/register-all")
    public ResponseEntity<ApiResponse<?>> registerAllConnectors() {
        try {
            registrationService.registerConnectors();
            
            return new ResponseEntity<>(
                    new ApiResponse<>("SUCCESS", "Đăng ký lại tất cả connector thành công", null),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ApiResponse<>("ERROR", "Lỗi khi đăng ký lại connector: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
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
} 