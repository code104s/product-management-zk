package com.nothing.onsite.productmanagementzk.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.util.WebAppInit;

public class CustomZkSpringWebAppInit implements WebAppInit, ServletContextListener {

    @Override
    public void init(WebApp wapp) throws Exception {
        ServletContext ctx = (ServletContext) wapp.getNativeContext();
        WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(ctx);
        wapp.setAttribute("SpringUtil.applicationContext", applicationContext, true);
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Không cần làm gì
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Không cần làm gì
    }
} 