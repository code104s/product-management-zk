package com.nothing.onsite.productmanagementzk.viewmodel;

import com.nothing.onsite.productmanagementzk.dao.CategoryDao;
import com.nothing.onsite.productmanagementzk.dao.ProductDao;
import com.nothing.onsite.productmanagementzk.model.Category;
import com.nothing.onsite.productmanagementzk.model.Product;
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
public class ProductViewModel {

    @WireVariable
    private ProductDao productDao;
    
    @WireVariable
    private CategoryDao categoryDao;
    
    private List<Product> productList;
    private Product selectedProduct;
    private Product newProduct = new Product();
    private List<Category> categoryList;
    
    @Init
    public void init() {
        loadData();
    }
    
    private void loadData() {
        try {
            productList = productDao.findAll();
            categoryList = categoryDao.findAll();
            
            // Đảm bảo newProduct có một category mặc định nếu có danh mục
            if (categoryList != null && !categoryList.isEmpty() && newProduct.getCategory() == null) {
                newProduct.setCategory(categoryList.get(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show("Lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi", Messagebox.OK, Messagebox.ERROR);
        }
    }
    
    @Command
    @NotifyChange({"productList", "selectedProduct", "newProduct"})
    public void saveProduct() {
        try {
            // Kiểm tra dữ liệu hợp lệ
            String validationError = validateProduct(newProduct);
            if (validationError != null) {
                Messagebox.show(validationError, "Lỗi", Messagebox.OK, Messagebox.ERROR);
                return;
            }
            
            if (newProduct.getId() == null) {
                productDao.save(newProduct);
            } else {
                // Sửa lỗi: Cập nhật sản phẩm đã chọn với dữ liệu từ newProduct
                selectedProduct.setName(newProduct.getName());
                selectedProduct.setDescription(newProduct.getDescription());
                selectedProduct.setPrice(newProduct.getPrice());
                selectedProduct.setStock(newProduct.getStock());
                selectedProduct.setImageUrl(newProduct.getImageUrl());
                selectedProduct.setCategory(newProduct.getCategory());
                productDao.save(selectedProduct);
            }
            loadData();
            newProduct = new Product();
            selectedProduct = null;
            
            // Đặt category mặc định cho sản phẩm mới
            if (categoryList != null && !categoryList.isEmpty()) {
                newProduct.setCategory(categoryList.get(0));
            }
            
            Messagebox.show("Lưu sản phẩm thành công!", "Thông báo", Messagebox.OK, Messagebox.INFORMATION);
        } catch (UncategorizedSQLException ex) {
            String errorMessage = ex.getMessage();
            String userMessage = "Lỗi khi lưu sản phẩm";
            
            // Phân tích lỗi SQL
            if (errorMessage.contains("Cannot set null to non-nullable column")) {
                if (errorMessage.contains("description")) {
                    userMessage = "Mô tả sản phẩm không được để trống";
                } else if (errorMessage.contains("name")) {
                    userMessage = "Tên sản phẩm không được để trống";
                } else if (errorMessage.contains("category_id")) {
                    userMessage = "Danh mục không được để trống";
                } else {
                    userMessage = "Một số trường bắt buộc không được để trống";
                }
            }
            
            ex.printStackTrace();
            Messagebox.show(userMessage, "Lỗi", Messagebox.OK, Messagebox.ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show("Lỗi khi lưu sản phẩm: " + e.getMessage(), "Lỗi", Messagebox.OK, Messagebox.ERROR);
        }
    }
    
    /**
     * Phương thức kiểm tra dữ liệu sản phẩm hợp lệ
     * @param product Sản phẩm cần kiểm tra
     * @return Chuỗi thông báo lỗi hoặc null nếu dữ liệu hợp lệ
     */
    private String validateProduct(Product product) {
        // Kiểm tra tên sản phẩm
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            return "Tên sản phẩm không được để trống";
        }
        if (product.getName().trim().length() < 2 || product.getName().trim().length() > 100) {
            return "Tên sản phẩm phải từ 2 đến 100 ký tự";
        }
        
        // Kiểm tra mô tả sản phẩm
        if (product.getDescription() == null || product.getDescription().trim().isEmpty()) {
            return "Mô tả sản phẩm không được để trống";
        }
        
        // Kiểm tra giá sản phẩm
        if (product.getPrice() < 0) {
            return "Giá sản phẩm không được âm";
        }
        
        // Kiểm tra số lượng tồn kho
        if (product.getStock() < 0) {
            return "Số lượng tồn kho không được âm";
        }
        
        // Kiểm tra danh mục
        if (product.getCategory() == null) {
            return "Vui lòng chọn danh mục cho sản phẩm";
        }
        
        // Kiểm tra URL hình ảnh (nếu có)
        if (product.getImageUrl() != null && !product.getImageUrl().trim().isEmpty()) {
            try {
                new java.net.URL(product.getImageUrl());
            } catch (java.net.MalformedURLException e) {
                return "URL hình ảnh không hợp lệ";
            }
        }
        
        return null; // Dữ liệu hợp lệ
    }
    
    @Command
    @NotifyChange({"selectedProduct", "newProduct"})
    public void editProduct(@BindingParam("product") Product product) {
        try {
            selectedProduct = product;
            newProduct = new Product();
            newProduct.setId(product.getId());
            newProduct.setName(product.getName());
            newProduct.setDescription(product.getDescription());
            newProduct.setPrice(product.getPrice());
            newProduct.setStock(product.getStock());
            newProduct.setImageUrl(product.getImageUrl());
            newProduct.setCategory(product.getCategory());
        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show("Lỗi khi chỉnh sửa sản phẩm: " + e.getMessage(), "Lỗi", Messagebox.OK, Messagebox.ERROR);
        }
    }
    
    @Command
    @NotifyChange({"productList", "selectedProduct", "newProduct"})
    public void deleteProduct(@BindingParam("product") Product product) {
        try {
            if (Messagebox.show("Bạn có chắc chắn muốn xóa sản phẩm này?", "Xác nhận", 
                    Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) == Messagebox.YES) {
                productDao.delete(product);
                loadData();
                selectedProduct = null;
                newProduct = new Product();
                
                // Đặt category mặc định cho sản phẩm mới
                if (categoryList != null && !categoryList.isEmpty()) {
                    newProduct.setCategory(categoryList.get(0));
                }
                
                Messagebox.show("Xóa sản phẩm thành công!", "Thông báo", Messagebox.OK, Messagebox.INFORMATION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show("Lỗi khi xóa sản phẩm: " + e.getMessage(), "Lỗi", Messagebox.OK, Messagebox.ERROR);
        }
    }
    
    @Command
    @NotifyChange({"selectedProduct", "newProduct"})
    public void newProduct() {
        try {
            selectedProduct = null;
            newProduct = new Product();
            
            // Đặt category mặc định cho sản phẩm mới
            if (categoryList != null && !categoryList.isEmpty()) {
                newProduct.setCategory(categoryList.get(0));
            }
            
            // Thêm log để debug
            System.out.println("New product created: " + newProduct);
        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show("Lỗi khi tạo sản phẩm mới: " + e.getMessage(), "Lỗi", Messagebox.OK, Messagebox.ERROR);
        }
    }

    public List<Product> getProductList() {
        return productList;
    }

    public Product getSelectedProduct() {
        return selectedProduct;
    }

    public void setSelectedProduct(Product selectedProduct) {
        this.selectedProduct = selectedProduct;
    }

    public Product getNewProduct() {
        return newProduct;
    }

    public void setNewProduct(Product newProduct) {
        this.newProduct = newProduct;
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }
} 