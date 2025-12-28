package com.kirkukhealth.poster.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Health Occasion Entity
 * كيان المناسبة الصحية
 * 
 * Represents global health awareness days/weeks
 * يمثل أيام/أسابيع التوعية الصحية العالمية
 */
@Entity
@Table(name = "health_occasions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthOccasion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "occasion_id")
    private String occasionId;

    @Column(name = "occasion_name_ar", nullable = false)
    private String occasionNameAr; // Arabic name

    @Column(name = "occasion_name_en")
    private String occasionNameEn; // English name

    @Column(name = "occasion_date", nullable = false)
    private LocalDate occasionDate; // Date of the occasion

    @Column(name = "is_week_long")
    @Builder.Default
    private Boolean isWeekLong = false; // If true, occasion spans a week

    @Column(name = "end_date")
    private LocalDate endDate; // End date for week-long occasions

    @Column(name = "category", length = 100)
    private String category; // e.g., "الأمراض المعدية", "الصحة النفسية"

    @Column(name = "description_ar", columnDefinition = "TEXT")
    private String descriptionAr; // Arabic description

    @Column(name = "suggested_topics", columnDefinition = "TEXT")
    private String suggestedTopics; // Comma-separated list of related topics

    @Column(name = "priority")
    @Builder.Default
    private Integer priority = 5; // 1-10, higher = more important

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true; // Whether to show this occasion

    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        updatedAt = java.time.LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = java.time.LocalDateTime.now();
    }
}


