package com.nothing.onsite.productmanagementzk.dao;

import com.nothing.onsite.productmanagementzk.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CategoryDao {

    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    public CategoryDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public List<Category> findAll() {
        String sql = "SELECT * FROM categories";
        return jdbcTemplate.query(sql, new CategoryRowMapper());
    }
    
    public Category findById(Long id) {
        String sql = "SELECT * FROM categories WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new CategoryRowMapper(), id);
    }
    
    public void save(Category category) {
        // Validate trước khi lưu
        validateCategory(category);
        
        // Kiểm tra trùng tên
        if (!isNameUnique(category)) {
            throw new IllegalArgumentException("Tên danh mục đã tồn tại: " + category.getName());
        }
        
        if (category.getId() == null || category.getId() == 0) {
            // Lấy ID tiếp theo từ bảng categories
            Long nextId = getNextId();
            category.setId(nextId);
            
            // Tạo mới
            String sql = "INSERT INTO categories (id, name, description) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, 
                category.getId(),
                category.getName(), 
                category.getDescription()
            );
            
            System.out.println("Đã tạo danh mục mới với ID: " + category.getId());
        } else {
            // Cập nhật
            String sql = "ALTER TABLE categories UPDATE name = ?, description = ? WHERE id = ?";
            jdbcTemplate.update(sql, category.getName(), category.getDescription(), category.getId());
            System.out.println("Đã cập nhật danh mục với ID: " + category.getId());
        }
    }
    
    /**
     * Kiểm tra tên danh mục đã tồn tại chưa
     * @param category Danh mục cần kiểm tra
     * @return true nếu tên chưa tồn tại, false nếu đã tồn tại
     */
    private boolean isNameUnique(Category category) {
        try {
            String sql = "SELECT COUNT(*) FROM categories WHERE LOWER(name) = LOWER(?) AND id != ?";
            Long id = category.getId() != null ? category.getId() : -1L;
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, category.getName(), id);
            return count != null && count == 0;
        } catch (Exception e) {
            // Nếu có lỗi, coi như tên đã tồn tại để an toàn
            return false;
        }
    }
    
    /**
     * Validate dữ liệu danh mục trước khi lưu
     * @param category Danh mục cần validate
     * @throws IllegalArgumentException nếu dữ liệu không hợp lệ
     */
    private void validateCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Danh mục không được null");
        }
        
        // Validate tên danh mục
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục không được để trống");
        }
        
        if (category.getName().trim().length() < 2) {
            throw new IllegalArgumentException("Tên danh mục phải có ít nhất 2 ký tự");
        }
        
        if (category.getName().trim().length() > 50) {
            throw new IllegalArgumentException("Tên danh mục không được vượt quá 50 ký tự");
        }
    }
    
    /**
     * Lấy ID tiếp theo cho bảng categories
     * @return ID tiếp theo
     */
    private Long getNextId() {
        try {
            // Lấy ID lớn nhất hiện tại
            Long maxId = jdbcTemplate.queryForObject("SELECT max(id) FROM categories", Long.class);
            // Nếu không có bản ghi nào, bắt đầu từ 1
            return maxId != null ? maxId + 1 : 1L;
        } catch (Exception e) {
            // Nếu có lỗi, bắt đầu từ 1
            System.out.println("Lỗi khi lấy ID tiếp theo: " + e.getMessage());
            return 1L;
        }
    }
    
    public void delete(Category category) {
        String sql = "ALTER TABLE categories DELETE WHERE id = ?";
        jdbcTemplate.update(sql, category.getId());
        System.out.println("Đã xóa danh mục với ID: " + category.getId());
    }
    
    public long count() {
        String sql = "SELECT count(*) FROM categories";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }
    
    private static class CategoryRowMapper implements RowMapper<Category> {
        @Override
        public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
            Category category = new Category();
            category.setId(rs.getLong("id"));
            category.setName(rs.getString("name"));
            category.setDescription(rs.getString("description"));
            return category;
        }
    }
} 