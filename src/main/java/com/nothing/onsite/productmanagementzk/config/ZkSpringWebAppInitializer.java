package com.nothing.onsite.productmanagementzk.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public class ZkSpringWebAppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        // Tạo Spring root context
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(ZkSpringConfig.class);
        
        // Đăng ký ContextLoaderListener
        servletContext.addListener(new ContextLoaderListener(rootContext));
        
        // Khởi tạo ZK Spring Integration
        servletContext.addListener(new CustomZkSpringWebAppInit());
    }
} 