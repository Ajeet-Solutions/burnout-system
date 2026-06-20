package com.bdos.burnout.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Jab bhi browser '/uploads/**' ka path maange, toh local C:/burnout/uploads/ folder se file uthao
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///C:/burnout/uploads/");
    }
}