package com.nothing.onsite.productmanagementzk.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    @ResponseBody
    public String handleError(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
        Exception exception = (Exception) request.getAttribute("jakarta.servlet.error.exception");
        
        return String.format("<html><body><h2>Lỗi %s</h2><div>%s</div><div>%s</div></body></html>",
                statusCode, exception != null ? exception.getMessage() : "Không tìm thấy trang", 
                "<a href='/'>Quay lại trang chủ</a>");
    }
} 