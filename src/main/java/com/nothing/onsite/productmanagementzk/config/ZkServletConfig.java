package com.nothing.onsite.productmanagementzk.config;

import jakarta.servlet.Servlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zkoss.zk.au.http.DHtmlUpdateServlet;
import org.zkoss.zk.ui.http.DHtmlLayoutServlet;
import org.zkoss.zk.ui.http.HttpSessionListener;

@Configuration
public class ZkServletConfig {

    @Bean
    public ServletRegistrationBean<Servlet> dHtmlLayoutServlet() {
        ServletRegistrationBean<Servlet> reg = new ServletRegistrationBean<>(
                new DHtmlLayoutServlet(), "*.zul", "*.zhtml");
        reg.setLoadOnStartup(1);
        reg.addInitParameter("update-uri", "/zkau");
        return reg;
    }

    @Bean
    public ServletRegistrationBean<Servlet> dHtmlUpdateServlet() {
        ServletRegistrationBean<Servlet> reg = new ServletRegistrationBean<>(
                new DHtmlUpdateServlet(), "/zkau/*");
        reg.setLoadOnStartup(2);
        return reg;
    }

    @Bean
    public HttpSessionListener httpSessionListener() {
        return new HttpSessionListener();
    }
    
    @Bean
    public CustomZkSpringWebAppInit customZkSpringWebAppInit() {
        return new CustomZkSpringWebAppInit();
    }
} 