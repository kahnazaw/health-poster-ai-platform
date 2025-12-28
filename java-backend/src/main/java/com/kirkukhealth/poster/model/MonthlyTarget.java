package com.kirkukhealth.poster.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Monthly Target Entity
 * كيان الهدف الشهري
 * 
 * Tracks monthly targets for health centers by category and topic
 * يتتبع الأهداف الشهرية للمراكز الصحية حسب الفئة والموضوع
 */
@Entity
@Table(name = "monthly_targets",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"center_id", "category_name", "topic_name", "target_month", "target_year"})
       })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyTarget {

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
     * Target for Individual Meetings
     * هدف اللقاءات الفردية
     */
    @Column(name = "target_meetings", nullable = false)
    @Builder.Default
    private Integer targetMeetings = 0;

    /**
     * Target for Lectures
     * هدف المحاضرات
     */
    @Column(name = "target_lectures", nullable = false)
    @Builder.Default
    private Integer targetLectures = 0;

    /**
     * Target for Seminars
     * هدف الندوات
     */
    @Column(name = "target_seminars", nullable = false)
    @Builder.Default
    private Integer targetSeminars = 0;

    /**
     * Target Month (1-12)
     * الشهر المستهدف (1-12)
     */
    @Column(name = "target_month", nullable = false)
    private Integer targetMonth;

    /**
     * Target Year
     * السنة المستهدفة
     */
    @Column(name = "target_year", nullable = false)
    private Integer targetYear;

    /**
     * Created timestamp
     * وقت الإنشاء
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Updated timestamp
     * وقت آخر تحديث
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (targetMeetings == null) targetMeetings = 0;
        if (targetLectures == null) targetLectures = 0;
        if (targetSeminars == null) targetSeminars = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

