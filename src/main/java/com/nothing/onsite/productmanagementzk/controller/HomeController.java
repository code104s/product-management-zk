package com.nothing.onsite.productmanagementzk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "forward:/web/index.zul";
    }

    @GetMapping("/product")
    public String product() {
        return "forward:/web/product.zul";
    }

    @GetMapping("/category")
    public String category() {
        return "forward:/web/category.zul";
    }

    @GetMapping("/chat")
    public String chat() {
        return "forward:/web/chat.zul";
    }
} 