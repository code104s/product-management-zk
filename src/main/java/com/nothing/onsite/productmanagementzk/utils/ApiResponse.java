package com.nothing.onsite.productmanagementzk.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class chứa response chuẩn cho API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private String result;    // SUCCESS hoặc ERROR
    private String message;   // Thông báo thành công hoặc lỗi
    private T data;           // Dữ liệu trả về từ service
} 