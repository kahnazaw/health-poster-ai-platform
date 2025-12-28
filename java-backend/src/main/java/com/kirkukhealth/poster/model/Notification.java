package com.kirkukhealth.poster.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Notification Entity for Admin
 * كيان الإشعارات للمدير
 * 
 * Tracks new poster submissions for admin review
 * يتتبع إرسالات البوسترات الجديدة لمراجعة المدير
 */
@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Notification type
     * نوع الإشعار
     */
    @Column(name = "type", nullable = false)
    @Builder.Default
    private String type = "POSTER_SUBMITTED"; // POSTER_SUBMITTED, POSTER_APPROVED, etc.

    /**
     * Poster ID (if related to a poster)
     * معرف البوستر (إذا كان متعلقاً ببوستر)
     */
    @Column(name = "poster_id")
    private String posterId;

    /**
     * User ID who triggered the notification
     * معرف المستخدم الذي أثار الإشعار
     */
    @Column(name = "user_id")
    private String userId;

    /**
     * Health Center Name
     * اسم المركز الصحي
     */
    @Column(name = "health_center_name", length = 200)
    private String healthCenterName;

    /**
     * Notification message
     * رسالة الإشعار
     */
    @Column(name = "message", length = 1000)
    private String message;

    /**
     * Whether notification has been read
     * هل تم قراءة الإشعار
     */
    @Column(name = "read", nullable = false)
    @Builder.Default
    private Boolean read = false;

    /**
     * Created timestamp
     * وقت الإنشاء
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (read == null) {
            read = false;
        }
        if (type == null) {
            type = "POSTER_SUBMITTED";
        }
    }
}

