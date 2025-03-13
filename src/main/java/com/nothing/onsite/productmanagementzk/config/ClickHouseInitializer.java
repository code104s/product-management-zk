package com.nothing.onsite.productmanagementzk.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Class khởi tạo schema ClickHouse
 */
@Component
public class ClickHouseInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(ClickHouseInitializer.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Value("${clickhouse.init-schema:false}")
    private boolean initSchema;
    
    @Override
    public void run(String... args) throws Exception {
        if (initSchema) {
            logger.info("Khởi tạo schema ClickHouse...");
            try {
                String schema = loadSchemaFromFile();
                String[] statements = schema.split(";");
                
                for (String statement : statements) {
                    if (!statement.trim().isEmpty()) {
                        logger.debug("Executing SQL: {}", statement);
                        jdbcTemplate.execute(statement);
                    }
                }
                
                logger.info("Khởi tạo schema ClickHouse thành công");
            } catch (Exception e) {
                logger.error("Lỗi khi khởi tạo schema ClickHouse", e);
                throw e;
            }
        } else {
            logger.info("Bỏ qua khởi tạo schema ClickHouse");
        }
    }
    
    private String loadSchemaFromFile() throws IOException {
        ClassPathResource resource = new ClassPathResource("schema.sql");
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }
} 