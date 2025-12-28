package com.kirkukhealth.poster.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Daily Statistics Entity for Health Centers
 * كيان الإحصائيات اليومية للمراكز الصحية
 * 
 * Tracks daily health promotion activities:
 * - Individual meetings
 * - Lectures
 * - Seminars
 * 
 * Linked to user_profiles (23 health centers)
 */
@Entity
@Table(name = "daily_statistics", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"center_id", "topic_name", "category_name", "entry_date"})
       })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Health Center ID (linked to user_profiles.user_id)
     * معرف المركز الصحي (مرتبط بـ user_profiles.user_id)
     */
    @Column(name = "center_id", nullable = false)
    private String centerId;

    /**
     * Category Name (11 main sections)
     * اسم الفئة (11 قسم رئيسي)
     */
    @Column(name = "category_name", nullable = false, length = 200)
    private String categoryName;

    /**
     * Topic Name (sub-topic under category)
     * اسم الموضوع (موضوع فرعي تحت الفئة)
     */
    @Column(name = "topic_name", nullable = false, length = 300)
    private String topicName;

    /**
     * Individual Meetings Count
     * عدد اللقاءات الفردية
     */
    @Column(name = "individual_meetings", nullable = false)
    @Builder.Default
    private Integer individualMeetings = 0;

    /**
     * Lectures Count
     * عدد المحاضرات
     */
    @Column(name = "lectures", nullable = false)
    @Builder.Default
    private Integer lectures = 0;

    /**
     * Seminars Count
     * عدد الندوات
     */
    @Column(name = "seminars", nullable = false)
    @Builder.Default
    private Integer seminars = 0;

    /**
     * Entry Date (date of the activity)
     * تاريخ الإدخال (تاريخ النشاط)
     */
    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    /**
     * Created timestamp
     * وقت الإنشاء
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;

    /**
     * Updated timestamp
     * وقت آخر تحديث
     */
    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        updatedAt = java.time.LocalDateTime.now();
        if (individualMeetings == null) individualMeetings = 0;
        if (lectures == null) lectures = 0;
        if (seminars == null) seminars = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = java.time.LocalDateTime.now();
    }
}

