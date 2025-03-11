package com.nothing.onsite.productmanagementzk.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Lớp tiện ích hỗ trợ validate dữ liệu
 */
@Component
public class ValidationUtils {
    
    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();
    
    /**
     * Validate một đối tượng theo các annotation Jakarta Validation
     * @param <T> Kiểu đối tượng
     * @param object Đối tượng cần validate
     * @return Map chứa tên trường và thông báo lỗi, rỗng nếu không có lỗi
     */
    public static <T> Map<String, String> validate(T object) {
        Map<String, String> errors = new HashMap<>();
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        
        for (ConstraintViolation<T> violation : violations) {
            String propertyPath = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(propertyPath, message);
        }
        
        return errors;
    }
    
    /**
     * Validate một đối tượng và ném ra ngoại lệ nếu có lỗi
     * @param <T> Kiểu đối tượng
     * @param object Đối tượng cần validate
     * @throws IllegalArgumentException nếu validate thất bại
     */
    public static <T> void validateAndThrow(T object) {
        Map<String, String> errors = validate(object);
        
        if (!errors.isEmpty()) {
            StringBuilder errorMessages = new StringBuilder("Lỗi validate dữ liệu: ");
            errors.forEach((field, message) -> 
                errorMessages.append(field).append(" - ").append(message).append("; ")
            );
            throw new IllegalArgumentException(errorMessages.toString());
        }
    }
    
    /**
     * Kiểm tra xem chuỗi có null hoặc rỗng không
     * @param str Chuỗi cần kiểm tra
     * @return true nếu chuỗi null hoặc rỗng, ngược lại false
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Kiểm tra độ dài chuỗi có nằm trong khoảng cho phép
     * @param str Chuỗi cần kiểm tra
     * @param min Độ dài tối thiểu
     * @param max Độ dài tối đa
     * @return true nếu độ dài nằm trong khoảng [min, max], ngược lại false
     */
    public static boolean isLengthValid(String str, int min, int max) {
        if (str == null) {
            return min == 0;
        }
        int length = str.trim().length();
        return length >= min && length <= max;
    }
    
    /**
     * Kiểm tra giá trị số có lớn hơn hoặc bằng giá trị tối thiểu
     * @param value Giá trị cần kiểm tra
     * @param min Giá trị tối thiểu
     * @return true nếu giá trị lớn hơn hoặc bằng min, ngược lại false
     */
    public static boolean isMinValid(double value, double min) {
        return value >= min;
    }
} 