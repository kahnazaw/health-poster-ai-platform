package com.kirkukhealth.poster.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Poster Entity with Governance Status
 * كيان البوستر مع حالة الحوكمة
 * 
 * Status Flow:
 * DRAFT -> PENDING -> APPROVED
 * 
 * Only Admin (Sector Manager) can approve posters
 * فقط المدير (مدير القطاع) يمكنه الموافقة على البوسترات
 */
@Entity
@Table(name = "posters")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Poster {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Poster Status: DRAFT, PENDING, APPROVED
     * حالة البوستر: مسودة، في الانتظار، معتمد
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private PosterStatus status = PosterStatus.DRAFT;

    /**
     * User ID who created the poster
     * معرف المستخدم الذي أنشأ البوستر
     */
    @Column(name = "user_id", nullable = false)
    private String userId;

    /**
     * Health Center ID (from user_profiles)
     * معرف المركز الصحي (من user_profiles)
     */
    @Column(name = "health_center_id")
    private String healthCenterId;

    /**
     * Poster title
     * عنوان البوستر
     */
    @Column(name = "title", length = 500)
    private String title;

    /**
     * Poster topic
     * موضوع البوستر
     */
    @Column(name = "topic", length = 200)
    private String topic;

    /**
     * Poster content (JSON)
     * محتوى البوستر (JSON)
     */
    @Column(name = "content", columnDefinition = "TEXT")
    private String content; // JSON string of PosterContent

    /**
     * High-resolution image bytes (300 DPI) - Only available when APPROVED
     * صورة عالية الدقة (300 DPI) - متاحة فقط عند الموافقة
     */
    @Lob
    @Column(name = "image_bytes", columnDefinition = "BYTEA")
    private byte[] imageBytes;

    /**
     * Whether content follows MOH guidelines
     * هل المحتوى يتبع إرشادات وزارة الصحة
     */
    @Column(name = "moh_approved")
    private Boolean mohApproved;

    /**
     * Admin who approved the poster (if approved)
     * المدير الذي وافق على البوستر (إذا تمت الموافقة)
     */
    @Column(name = "approved_by")
    private String approvedBy;

    /**
     * Approval timestamp
     * وقت الموافقة
     */
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

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
        if (status == null) {
            status = PosterStatus.DRAFT;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Poster Status Enum
     * تعداد حالة البوستر
     */
    public enum PosterStatus {
        DRAFT,      // مسودة - يمكن التعديل
        PENDING,    // في الانتظار - ينتظر الموافقة
        APPROVED    // معتمد - جاهز للطباعة بجودة 300 DPI
    }
}

