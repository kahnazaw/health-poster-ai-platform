package com.kirkukhealth.poster.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Bulletin Entity (Official MOH Directives)
 * كيان النشرة الرسمية (تعليمات وزارة الصحة)
 * 
 * Read-only section for Admin to post MOH directives to all 23 centers
 * قسم للقراءة فقط للمدير لنشر تعليمات وزارة الصحة لجميع المراكز الـ 23
 */
@Entity
@Table(name = "bulletins")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bulletin {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Bulletin Title
     * عنوان النشرة
     */
    @Column(name = "title", nullable = false, length = 500)
    private String title;

    /**
     * Bulletin Content
     * محتوى النشرة
     */
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * Admin ID who posted the bulletin
     * معرف المدير الذي نشر النشرة
     */
    @Column(name = "posted_by", nullable = false)
    private String postedBy;

    /**
     * Priority: HIGH, NORMAL, LOW
     * الأولوية: عالية، عادية، منخفضة
     */
    @Column(name = "priority", length = 20)
    @Builder.Default
    private String priority = "NORMAL";

    /**
     * Whether bulletin is pinned (shown at top)
     * هل النشرة مثبتة (تظهر في الأعلى)
     */
    @Column(name = "is_pinned")
    @Builder.Default
    private Boolean isPinned = false;

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
        if (priority == null) priority = "NORMAL";
        if (isPinned == null) isPinned = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Priority Enum
     */
    public enum Priority {
        HIGH, NORMAL, LOW
    }
}

