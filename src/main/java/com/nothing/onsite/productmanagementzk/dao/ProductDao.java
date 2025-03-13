package com.nothing.onsite.productmanagementzk.dao;

import com.nothing.onsite.productmanagementzk.model.Category;
import com.nothing.onsite.productmanagementzk.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ProductDao {

    private final JdbcTemplate jdbcTemplate;
    private final CategoryDao categoryDao;
    
    @Autowired
    public ProductDao(JdbcTemplate jdbcTemplate, CategoryDao categoryDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.categoryDao = categoryDao;
    }
    
    public List<Product> findAll() {
        String sql = "SELECT * FROM products";
        return jdbcTemplate.query(sql, new ProductRowMapper());
    }
    
    public Product findById(Long id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new ProductRowMapper(), id);
    }
    
    public List<Product> findByCategoryId(Long categoryId) {
        String sql = "SELECT * FROM products WHERE category_id = ?";
        return jdbcTemplate.query(sql, new ProductRowMapper(), categoryId);
    }
    
    public void save(Product product) {
        // Validate trước khi lưu
        validateProduct(product);
        
        if (product.getId() == null || product.getId() == 0) {
            // Lấy ID tiếp theo từ bảng products
            Long nextId = getNextId();
            product.setId(nextId);
            
            // Tạo mới
            String sql = "INSERT INTO products (id, name, description, price, stock, image_url, category_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, 
                product.getId(),
                product.getName(), 
                product.getDescription(), 
                product.getPrice(), 
                product.getStock(), 
                product.getImageUrl(), 
                product.getCategory().getId()
            );
            
            System.out.println("Đã tạo sản phẩm mới với ID: " + product.getId());
        } else {
            // Cập nhật
            String sql = "ALTER TABLE products UPDATE name = ?, description = ?, price = ?, stock = ?, image_url = ?, category_id = ? WHERE id = ?";
            jdbcTemplate.update(sql, 
                product.getName(), 
                product.getDescription(), 
                product.getPrice(), 
                product.getStock(), 
                product.getImageUrl(), 
                product.getCategory().getId(),
                product.getId()
            );
            System.out.println("Đã cập nhật sản phẩm với ID: " + product.getId());
        }
    }
    
    /**
     * Lấy ID tiếp theo cho bảng products
     * @return ID tiếp theo
     */
    private Long getNextId() {
        try {
            // Lấy ID lớn nhất hiện tại
            Long maxId = jdbcTemplate.queryForObject("SELECT max(id) FROM products", Long.class);
            // Nếu không có bản ghi nào, bắt đầu từ 1
            return maxId != null ? maxId + 1 : 1L;
        } catch (Exception e) {
            // Nếu có lỗi, bắt đầu từ 1
            System.out.println("Lỗi khi lấy ID tiếp theo: " + e.getMessage());
            return 1L;
        }
    }
    
    /**
     * Validate dữ liệu sản phẩm trước khi lưu
     * @param product Sản phẩm cần validate
     * @throws IllegalArgumentException nếu dữ liệu không hợp lệ
     */
    private void validateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Sản phẩm không được null");
        }
        
        // Validate tên sản phẩm
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống");
        }
        
        // Validate mô tả (không được null trong DB)
        if (product.getDescription() == null) {
            throw new IllegalArgumentException("Mô tả sản phẩm không được để trống");
        }
        
        // Validate giá
        if (product.getPrice() < 0) {
            throw new IllegalArgumentException("Giá sản phẩm không được âm");
        }
        
        // Validate số lượng
        if (product.getStock() < 0) {
            throw new IllegalArgumentException("Số lượng tồn kho không được âm");
        }
        
        // Validate danh mục
        if (product.getCategory() == null) {
            throw new IllegalArgumentException("Danh mục không được để trống");
        }
    }
    
    public void delete(Product product) {
        String sql = "ALTER TABLE products DELETE WHERE id = ?";
        jdbcTemplate.update(sql, product.getId());
        System.out.println("Đã xóa sản phẩm với ID: " + product.getId());
    }
    
    public long count() {
        String sql = "SELECT count(*) FROM products";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }
    
    private class ProductRowMapper implements RowMapper<Product> {
        @Override
        public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
            Product product = new Product();
            product.setId(rs.getLong("id"));
            product.setName(rs.getString("name"));
            product.setDescription(rs.getString("description"));
            product.setPrice(rs.getDouble("price"));
            product.setStock(rs.getInt("stock"));
            product.setImageUrl(rs.getString("image_url"));
            
            // Lấy category
            Long categoryId = rs.getLong("category_id");
            Category category = categoryDao.findById(categoryId);
            product.setCategory(category);
            
            return product;
        }
    }
} 