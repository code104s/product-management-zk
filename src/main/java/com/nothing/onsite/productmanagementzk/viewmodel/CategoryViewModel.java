package com.nothing.onsite.productmanagementzk.viewmodel;

import com.nothing.onsite.productmanagementzk.model.Category;
import com.nothing.onsite.productmanagementzk.repository.CategoryRepository;
import org.springframework.stereotype.Component;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Messagebox;

import java.util.List;

@Component
@VariableResolver(DelegatingVariableResolver.class)
public class CategoryViewModel {

    @WireVariable
    private CategoryRepository categoryRepository;
    
    private List<Category> categoryList;
    private Category selectedCategory;
    private Category newCategory = new Category();
    
    @Init
    public void init() {
        loadData();
    }
    
    private void loadData() {
        try {
            categoryList = categoryRepository.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show("Lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi", Messagebox.OK, Messagebox.ERROR);
        }
    }
    
    @Command
    @NotifyChange({"categoryList", "selectedCategory", "newCategory"})
    public void saveCategory() {
        try {
            if (newCategory.getId() == null) {
                categoryRepository.save(newCategory);
            } else {
                // Sửa lỗi: Cập nhật danh mục đã chọn với dữ liệu từ newCategory
                selectedCategory.setName(newCategory.getName());
                selectedCategory.setDescription(newCategory.getDescription());
                categoryRepository.save(selectedCategory);
            }
            loadData();
            newCategory = new Category();
            selectedCategory = null;
            
            Messagebox.show("Lưu danh mục thành công!", "Thông báo", Messagebox.OK, Messagebox.INFORMATION);
        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show("Lỗi khi lưu danh mục: " + e.getMessage(), "Lỗi", Messagebox.OK, Messagebox.ERROR);
        }
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
                categoryRepository.delete(category);
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