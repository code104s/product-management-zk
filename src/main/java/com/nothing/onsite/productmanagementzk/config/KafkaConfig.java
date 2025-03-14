package com.nothing.onsite.productmanagementzk.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

/**
 * Cấu hình Kafka
 */
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.topic.device-data}")
    private String deviceDataTopic;

    @Value("${kafka.topic.device-alarm}")
    private String deviceAlarmTopic;

    /**
     * Cấu hình KafkaAdmin
     */
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    /**
     * Tạo topic device-data
     */
    @Bean
    public NewTopic deviceDataTopic() {
        return new NewTopic(deviceDataTopic, 3, (short) 1);
    }

    /**
     * Tạo topic device-alarm
     */
    @Bean
    public NewTopic deviceAlarmTopic() {
        return new NewTopic(deviceAlarmTopic, 3, (short) 1);
    }
} 