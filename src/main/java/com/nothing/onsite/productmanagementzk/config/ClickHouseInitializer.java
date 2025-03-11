package com.nothing.onsite.productmanagementzk.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.sql.SQLException;

@Component
@EnableRetry
public class ClickHouseInitializer {
    
    private static final Logger logger = LoggerFactory.getLogger(ClickHouseInitializer.class);
    
    private final JdbcTemplate jdbcTemplate;
    
    @Value("${clickhouse.init-schema:true}")
    private boolean initSchema;
    
    @Autowired
    public ClickHouseInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @PostConstruct
    @Retryable(value = {Exception.class}, maxAttempts = 10, backoff = @Backoff(delay = 10000, multiplier = 2))
    public void initialize() {
        if (!initSchema) {
            logger.info("Bỏ qua khởi tạo schema ClickHouse (clickhouse.init-schema=false)");
            return;
        }
        
        try {
            logger.info("Kiểm tra kết nối đến ClickHouse...");
            jdbcTemplate.execute("SELECT 1");
            logger.info("Kết nối đến ClickHouse thành công!");
            
            logger.info("Khởi tạo cơ sở dữ liệu ClickHouse...");
            
            // Tạo bảng categories
            try {
                jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS categories (" +
                        "id Int64, " +
                        "name String, " +
                        "description String, " +
                        "PRIMARY KEY (id)" +
                        ") ENGINE = MergeTree() ORDER BY id");
                logger.info("Bảng categories đã được tạo hoặc đã tồn tại");
            } catch (Exception e) {
                logger.error("Lỗi khi tạo bảng categories: " + e.getMessage(), e);
            }
            
            // Tạo bảng products
            try {
                jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS products (" +
                        "id Int64, " +
                        "name String, " +
                        "description String, " +
                        "price Decimal(10,2), " +
                        "stock Int32, " +
                        "image_url String, " +
                        "category_id Int64, " +
                        "PRIMARY KEY (id)" +
                        ") ENGINE = MergeTree() ORDER BY id");
                logger.info("Bảng products đã được tạo hoặc đã tồn tại");
            } catch (Exception e) {
                logger.error("Lỗi khi tạo bảng products: " + e.getMessage(), e);
            }
            
            // Tạo bảng messages
            try {
                jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS messages (" +
                        "id Int64, " +
                        "sender String, " +
                        "content String, " +
                        "timestamp DateTime, " +
                        "PRIMARY KEY (id)" +
                        ") ENGINE = MergeTree() ORDER BY id");
                logger.info("Bảng messages đã được tạo hoặc đã tồn tại");
            } catch (Exception e) {
                logger.error("Lỗi khi tạo bảng messages: " + e.getMessage(), e);
            }
            
            logger.info("Khởi tạo cơ sở dữ liệu ClickHouse thành công!");
        } catch (Exception e) {
            logger.error("Lỗi khi khởi tạo cơ sở dữ liệu ClickHouse: " + e.getMessage(), e);
            throw e;
        }
    }
} 