package com.kirkukhealth.poster.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * FAQ Entry Entity (Knowledge Base)
 * كيان إدخال الأسئلة الشائعة (قاعدة المعرفة)
 * 
 * Searchable section with pre-defined Q&A for common tasks and medical protocols
 * قسم قابل للبحث مع أسئلة وأجوبة محددة مسبقاً للمهام الشائعة والبروتوكولات الطبية
 */
@Entity
@Table(name = "faq_entries")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FAQEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Question
     * السؤال
     */
    @Column(name = "question", nullable = false, columnDefinition = "TEXT")
    private String question;

    /**
     * Answer
     * الجواب
     */
    @Column(name = "answer", nullable = false, columnDefinition = "TEXT")
    private String answer;

    /**
     * Category (e.g., "Poster Generation", "Statistics", "MOH Guidelines")
     * الفئة (مثل: "توليد البوسترات", "الإحصائيات", "إرشادات وزارة الصحة")
     */
    @Column(name = "category", length = 200)
    private String category;

    /**
     * Tags for search (comma-separated)
     * علامات للبحث (مفصولة بفواصل)
     */
    @Column(name = "tags", length = 500)
    private String tags;

    /**
     * View count
     * عدد المشاهدات
     */
    @Column(name = "view_count")
    @Builder.Default
    private Integer viewCount = 0;

    /**
     * Whether entry is featured (shown prominently)
     * هل الإدخال مميز (يظهر بشكل بارز)
     */
    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;

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
        if (viewCount == null) viewCount = 0;
        if (isFeatured == null) isFeatured = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

