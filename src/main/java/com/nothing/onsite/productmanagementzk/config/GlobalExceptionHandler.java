package com.nothing.onsite.productmanagementzk.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.jdbc.UncategorizedSQLException;

import java.util.HashMap;
import java.util.Map;

/**
 * Xử lý ngoại lệ toàn cục cho ứng dụng
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Xử lý các ngoại lệ IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Lỗi validate dữ liệu");
        errorResponse.put("message", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Xử lý các ngoại lệ MethodArgumentNotValidException
     * Xảy ra khi validation của @Valid thất bại
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        
        errorResponse.put("error", "Lỗi validate dữ liệu");
        errorResponse.put("details", errors);
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Xử lý lỗi SQL uncategorized, chuyển đổi lỗi SQL thành thông báo dễ hiểu
     */
    @ExceptionHandler(UncategorizedSQLException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleUncategorizedSQLException(UncategorizedSQLException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        String message = ex.getMessage();
        
        // Xử lý lỗi NULL cho cột không được NULL
        if (message.contains("Cannot set null to non-nullable column")) {
            // Phân tích thông báo lỗi để trích xuất tên cột
            String columnInfo = message.substring(message.indexOf("column") + 7);
            String columnName = "không xác định";
            if (columnInfo.contains("[")) {
                columnName = columnInfo.substring(columnInfo.indexOf("[") + 1, columnInfo.indexOf("]"));
                // Lấy tên cột từ số thứ tự (nếu có)
                if (columnName.contains(" ")) {
                    columnName = columnName.substring(columnName.indexOf(" ") + 1);
                }
            }
            
            // Chuyển đổi tên cột thành thông báo người dùng
            switch (columnName.toLowerCase()) {
                case "name":
                    message = "Tên sản phẩm không được để trống";
                    break;
                case "description":
                    message = "Mô tả sản phẩm không được để trống";
                    break;
                case "category_id":
                    message = "Danh mục không được để trống";
                    break;
                default:
                    message = "Một số trường bắt buộc không được để trống";
            }
        }
        
        errorResponse.put("error", "Lỗi khi lưu dữ liệu");
        errorResponse.put("message", message);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Xử lý ngoại lệ chung
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(HttpServletRequest req, Exception ex) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", ex);
        mav.addObject("url", req.getRequestURL());
        mav.addObject("message", ex.getMessage());
        mav.setViewName("error");
        return mav;
    }
} 