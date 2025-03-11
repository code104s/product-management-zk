package com.nothing.onsite.productmanagementzk.config;

import com.nothing.onsite.productmanagementzk.converter.FormattedDateConverter;
import com.nothing.onsite.productmanagementzk.converter.FormattedNumberConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;

@Configuration
@ComponentScan(basePackages = {"com.nothing.onsite.productmanagementzk.viewmodel"})
public class ZkSpringConfig {
    
    @Bean
    public DelegatingVariableResolver delegatingVariableResolver() {
        return new DelegatingVariableResolver();
    }
    
    @Bean
    public FormattedNumberConverter formattedNumberConverter() {
        return new FormattedNumberConverter();
    }
    
    @Bean
    public FormattedDateConverter formattedDateConverter() {
        return new FormattedDateConverter();
    }
} 