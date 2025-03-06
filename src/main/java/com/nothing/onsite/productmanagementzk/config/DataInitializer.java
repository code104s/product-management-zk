package com.nothing.onsite.productmanagementzk.config;

import com.nothing.onsite.productmanagementzk.model.Category;
import com.nothing.onsite.productmanagementzk.model.Product;
import com.nothing.onsite.productmanagementzk.repository.CategoryRepository;
import com.nothing.onsite.productmanagementzk.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Autowired
    public DataInitializer(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        try {
            // Kiểm tra nếu đã có dữ liệu
            if (categoryRepository.count() == 0) {
                // Tạo các danh mục
                Category electronics = new Category();
                electronics.setName("Điện tử");
                electronics.setDescription("Các sản phẩm điện tử, công nghệ");
                categoryRepository.save(electronics);

                Category clothing = new Category();
                clothing.setName("Thời trang");
                clothing.setDescription("Quần áo, giày dép, phụ kiện");
                categoryRepository.save(clothing);

                Category books = new Category();
                books.setName("Sách");
                books.setDescription("Sách, truyện, tài liệu");
                categoryRepository.save(books);

                // Tạo các sản phẩm mẫu
                Product laptop = new Product();
                laptop.setName("MacBook Air 2025");
                laptop.setDescription("MacBook Air là máy tính xách tay bán chạy nhất và một trong những sản phẩm quan trọng nhất của Apple.");
                laptop.setPrice(50000000);
                laptop.setStock(10);
                laptop.setImageUrl("https://mtg.1cdn.vn/2025/03/05/apple-ra-mat-macbook-air-2025-dung-chip-m4-gia-giam-100-usd-du-ong-trump-ap-thue-bo-sung-voi-trung-quoc.jpg");
                laptop.setCategory(electronics);
                productRepository.save(laptop);

                Product phone = new Product();
                phone.setName("iPhone 16 Pro");
                phone.setDescription("Điện thoại thông minh với camera chất lượng cao");
                phone.setPrice(20000000);
                phone.setStock(15);
                phone.setImageUrl("https://store.storeimages.cdn-apple.com/4982/as-images.apple.com/is/iphone-13-pro-family-hero?wid=940&hei=1112&fmt=png-alpha&.v=1631220221000");
                phone.setCategory(electronics);
                productRepository.save(phone);

                Product tshirt = new Product();
                tshirt.setName("Áo thun nam");
                tshirt.setDescription("Áo thun cotton 100%, thoáng mát");
                tshirt.setPrice(250000);
                tshirt.setStock(50);
                tshirt.setImageUrl("https://product.hstatic.net/1000360022/product/trang_trn0035_1_20220607161159_9ad7c1a0d2a44d6e9c00a56e7ca12be2.jpg");
                tshirt.setCategory(clothing);
                productRepository.save(tshirt);

                Product novel = new Product();
                novel.setName("Nhà giả kim");
                novel.setDescription("Tiểu thuyết nổi tiếng của Paulo Coelho");
                novel.setPrice(120000);
                novel.setStock(30);
                novel.setImageUrl("https://salt.tikicdn.com/cache/w1200/ts/product/45/3b/fc/aa81d0a534b45706ae1eee1e344e80d9.jpg");
                novel.setCategory(books);
                productRepository.save(novel);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi khởi tạo dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 