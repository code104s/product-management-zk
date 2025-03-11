package com.nothing.onsite.productmanagementzk.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DataInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            // Kiểm tra nếu đã có dữ liệu
            int categoryCount = jdbcTemplate.queryForObject("SELECT count(*) FROM categories", Integer.class);
            
            if (categoryCount == 0) {
                logger.info("Khởi tạo dữ liệu mẫu...");
                
                // Tạo các danh mục
                jdbcTemplate.update(
                    "INSERT INTO categories (id, name, description) VALUES (?, ?, ?)",
                    1, "Điện tử", "Các sản phẩm điện tử, công nghệ"
                );
                
                jdbcTemplate.update(
                    "INSERT INTO categories (id, name, description) VALUES (?, ?, ?)",
                    2, "Thời trang", "Quần áo, giày dép, phụ kiện"
                );
                
                jdbcTemplate.update(
                    "INSERT INTO categories (id, name, description) VALUES (?, ?, ?)",
                    3, "Sách", "Sách, truyện, tài liệu"
                );
                
                // Tạo các sản phẩm mẫu
                jdbcTemplate.update(
                    "INSERT INTO products (id, name, description, price, stock, image_url, category_id) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    1, "MacBook Air 2025", "MacBook Air là máy tính xách tay bán chạy nhất và một trong những sản phẩm quan trọng nhất của Apple.",
                    50000000, 10, "https://mtg.1cdn.vn/2025/03/05/apple-ra-mat-macbook-air-2025-dung-chip-m4-gia-giam-100-usd-du-ong-trump-ap-thue-bo-sung-voi-trung-quoc.jpg", 1
                );
                
                jdbcTemplate.update(
                    "INSERT INTO products (id, name, description, price, stock, image_url, category_id) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    2, "iPhone 16 Pro", "Điện thoại thông minh với camera chất lượng cao",
                    20000000, 15, "https://store.storeimages.cdn-apple.com/4982/as-images.apple.com/is/iphone-13-pro-family-hero?wid=940&hei=1112&fmt=png-alpha&.v=1631220221000", 1
                );
                
                jdbcTemplate.update(
                    "INSERT INTO products (id, name, description, price, stock, image_url, category_id) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    3, "Áo thun nam", "Áo thun cotton 100%, thoáng mát",
                    250000, 50, "https://product.hstatic.net/1000360022/product/trang_trn0035_1_20220607161159_9ad7c1a0d2a44d6e9c00a56e7ca12be2.jpg", 2
                );
                
                jdbcTemplate.update(
                    "INSERT INTO products (id, name, description, price, stock, image_url, category_id) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    4, "Nhà giả kim", "Tiểu thuyết nổi tiếng của Paulo Coelho",
                    120000, 30, "https://salt.tikicdn.com/cache/w1200/ts/product/45/3b/fc/aa81d0a534b45706ae1eee1e344e80d9.jpg", 3
                );
                
                logger.info("Khởi tạo dữ liệu mẫu thành công!");
            } else {
                logger.info("Dữ liệu đã tồn tại, bỏ qua khởi tạo.");
            }
        } catch (Exception e) {
            logger.error("Lỗi khi khởi tạo dữ liệu: " + e.getMessage(), e);
        }
    }
} 