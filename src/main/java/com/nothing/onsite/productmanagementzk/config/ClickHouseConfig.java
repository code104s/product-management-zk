package com.nothing.onsite.productmanagementzk.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class ClickHouseConfig {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;
    
    @Value("${clickhouse.connection-timeout:30000}")
    private int connectionTimeout;
    
    @Value("${clickhouse.socket-timeout:30000}")
    private int socketTimeout;

    @Bean
    @Primary
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        
        // Các tham số kết nối đã được thêm vào URL, không cần thiết lập ở đây
        
        return dataSource;
    }
    
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.setQueryTimeout(60); // Tăng timeout lên 60 giây
        return jdbcTemplate;
    }
    
    // Phương thức này sẽ được gọi khi ứng dụng khởi động để tạo các bảng cần thiết
    @Bean
    public ClickHouseInitializer clickHouseInitializer(JdbcTemplate jdbcTemplate) {
        return new ClickHouseInitializer(jdbcTemplate);
    }
} 