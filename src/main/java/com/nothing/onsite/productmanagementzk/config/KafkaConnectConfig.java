package com.nothing.onsite.productmanagementzk.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Cấu hình Kafka Connect
 * 
 * Lưu ý: Connectors sẽ được quản lý bởi KafkaConnectorRegistrationService 
 * và các file JSON trong resources/kafka-connect thay vì các bean trong class này
 */
@Configuration
public class KafkaConnectConfig {

    @Value("${kafka.connect.bootstrap.servers}")
    private String bootstrapServers;

    @Value("${kafka.connect.key.converter}")
    private String keyConverter;

    @Value("${kafka.connect.value.converter}")
    private String valueConverter;

    @Value("${kafka.connect.key.converter.schemas.enable:false}")
    private boolean keyConverterSchemasEnable;

    @Value("${kafka.connect.value.converter.schemas.enable:false}")
    private boolean valueConverterSchemasEnable;

    @Value("${spring.datasource.url}")
    private String clickhouseJdbcUrl;

    @Value("${spring.datasource.username}")
    private String clickhouseUsername;

    @Value("${spring.datasource.password}")
    private String clickhousePassword;
} 