package com.nothing.onsite.productmanagementzk.viewmodel;

import com.nothing.onsite.productmanagementzk.dao.CategoryDao;
import com.nothing.onsite.productmanagementzk.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Messagebox;
import org.springframework.jdbc.UncategorizedSQLException;

import java.util.List;

@Component
@VariableResolver(DelegatingVariableResolver.class)
public class CategoryViewModel {

    @WireVariable
    private CategoryDao categoryDao;
    
    private List<Category> categoryList;
    private Category selectedCategory;
    private Category newCategory = new Category();
    
    @Init
    public void init() {
        loadData();
    }
    
    private void loadData() {
        try {
            categoryList = categoryDao.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show("Lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi", Messagebox.OK, Messagebox.ERROR);
        }
    }
    
    @Command
    @NotifyChange({"categoryList", "selectedCategory", "newCategory"})
    public void saveCategory() {
        try {
            // Kiểm tra dữ liệu hợp lệ
            String validationError = validateCategory(newCategory);
            if (validationError != null) {
                Messagebox.show(validationError, "Lỗi", Messagebox.OK, Messagebox.ERROR);
                return;
            }
            
            // Kiểm tra trùng tên danh mục
            if (isDuplicateCategoryName(newCategory)) {
                Messagebox.show("Tên danh mục đã tồn tại", "Lỗi", Messagebox.OK, Messagebox.ERROR);
                return;
            }
            
            if (newCategory.getId() == null) {
                categoryDao.save(newCategory);
            } else {
                // Sửa lỗi: Cập nhật danh mục đã chọn với dữ liệu từ newCategory
                selectedCategory.setName(newCategory.getName());
                selectedCategory.setDescription(newCategory.getDescription());
                categoryDao.save(selectedCategory);
            }
            loadData();
            newCategory = new Category();
            selectedCategory = null;
            
            Messagebox.show("Lưu danh mục thành công!", "Thông báo", Messagebox.OK, Messagebox.INFORMATION);
        } catch (UncategorizedSQLException ex) {
            String errorMessage = ex.getMessage();
            String userMessage = "Lỗi khi lưu danh mục";
            
            // Phân tích lỗi SQL
            if (errorMessage.contains("Cannot set null to non-nullable column")) {
                if (errorMessage.contains("name")) {
                    userMessage = "Tên danh mục không được để trống";
                } else if (errorMessage.contains("description")) {
                    userMessage = "Mô tả danh mục không được để trống";
                } else {
                    userMessage = "Một số trường bắt buộc không được để trống";
                }
            } else if (errorMessage.contains("UNIQUE constraint failed")) {
                userMessage = "Tên danh mục đã tồn tại";
            }
            
            ex.printStackTrace();
            Messagebox.show(userMessage, "Lỗi", Messagebox.OK, Messagebox.ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show("Lỗi khi lưu danh mục: " + e.getMessage(), "Lỗi", Messagebox.OK, Messagebox.ERROR);
        }
    }
    
    /**
     * Phương thức kiểm tra dữ liệu danh mục hợp lệ
     * @param category Danh mục cần kiểm tra
     * @return Chuỗi thông báo lỗi hoặc null nếu dữ liệu hợp lệ
     */
    private String validateCategory(Category category) {
        // Kiểm tra tên danh mục
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            return "Tên danh mục không được để trống";
        }
        if (category.getName().trim().length() < 2 || category.getName().trim().length() > 50) {
            return "Tên danh mục phải từ 2 đến 50 ký tự";
        }
        
        return null; // Dữ liệu hợp lệ
    }
    
    /**
     * Kiểm tra trùng tên danh mục
     * @param category Danh mục cần kiểm tra
     * @return true nếu tên đã tồn tại, false nếu không trùng
     */
    private boolean isDuplicateCategoryName(Category category) {
        if (category.getId() != null) {
            // Trường hợp cập nhật: Kiểm tra nếu tên mới trùng với tên của danh mục khác
            for (Category existingCategory : categoryList) {
                if (!existingCategory.getId().equals(category.getId()) && 
                    existingCategory.getName().equalsIgnoreCase(category.getName())) {
                    return true;
                }
            }
        } else {
            // Trường hợp thêm mới: Kiểm tra nếu tên đã tồn tại
            for (Category existingCategory : categoryList) {
                if (existingCategory.getName().equalsIgnoreCase(category.getName())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Command
    @NotifyChange({"selectedCategory", "newCategory"})
    public void editCategory(@BindingParam("category") Category category) {
        try {
            selectedCategory = category;
            newCategory = new Category();
            newCategory.setId(category.getId());
            newCategory.setName(category.getName());
            newCategory.setDescription(category.getDescription());
        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show("Lỗi khi chỉnh sửa danh mục: " + e.getMessage(), "Lỗi", Messagebox.OK, Messagebox.ERROR);
        }
    }
    
    @Command
    @NotifyChange({"categoryList", "selectedCategory", "newCategory"})
    public void deleteCategory(@BindingParam("category") Category category) {
        try {
            if (Messagebox.show("Bạn có chắc chắn muốn xóa danh mục này?", "Xác nhận", 
                    Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) == Messagebox.YES) {
                categoryDao.delete(category);
                loadData();
                selectedCategory = null;
                newCategory = new Category();
                
                Messagebox.show("Xóa danh mục thành công!", "Thông báo", Messagebox.OK, Messagebox.INFORMATION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show("Lỗi khi xóa danh mục: " + e.getMessage(), "Lỗi", Messagebox.OK, Messagebox.ERROR);
        }
    }
    
    @Command
    @NotifyChange({"selectedCategory", "newCategory"})
    public void newCategory() {
        try {
            selectedCategory = null;
            newCategory = new Category();
            
            // Thêm log để debug
            System.out.println("New category created: " + newCategory);
        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show("Lỗi khi tạo danh mục mới: " + e.getMessage(), "Lỗi", Messagebox.OK, Messagebox.ERROR);
        }
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public Category getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(Category selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public Category getNewCategory() {
        return newCategory;
    }

    public void setNewCategory(Category newCategory) {
        this.newCategory = newCategory;
    }
} 