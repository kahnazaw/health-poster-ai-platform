package com.kirkukhealth.poster.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Application Configuration
 * إعدادات التطبيق
 */
@Configuration
public class AppConfig {

    /**
     * ObjectMapper bean for JSON serialization/deserialization
     * كائن ObjectMapper لتحويل JSON
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}

