package com.nothing.onsite.productmanagementzk.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(min = 2, max = 100, message = "Tên sản phẩm phải từ 2 đến 100 ký tự")
    @Column(nullable = false)
    private String name;
    
    @NotNull(message = "Mô tả sản phẩm không được để trống")
    private String description;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "Giá sản phẩm không được âm")
    private double price;
    
    @Min(value = 0, message = "Số lượng tồn kho không được âm")
    private int stock;
    
    private String imageUrl;
    
    @NotNull(message = "Danh mục không được để trống")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;
    
    // Thêm phương thức toString để hỗ trợ hiển thị trong combobox
    @Override
    public String toString() {
        return name;
    }
}
