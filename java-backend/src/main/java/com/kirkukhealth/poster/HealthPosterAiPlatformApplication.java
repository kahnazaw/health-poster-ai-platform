package com.kirkukhealth.poster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Health Poster AI Platform - Java Backend
 * 
 * منصة مؤسسية رسمية لتوليد بوسترات التوعية الصحية بالذكاء الاصطناعي
 * 
 * Specialized for Kirkuk Health Directorate - First Sector
 * مخصصة لدائرة صحة كركوك – قطاع كركوك الأول
 */
@SpringBootApplication
@EnableConfigurationProperties
public class HealthPosterAiPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthPosterAiPlatformApplication.class, args);
    }
}

