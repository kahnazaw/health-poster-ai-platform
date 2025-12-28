package com.kirkukhealth.poster.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Poster Comment Entity
 * كيان تعليق البوستر
 * 
 * Comments on posters linked to the approval workflow
 * تعليقات على البوسترات مرتبطة بسير عمل الموافقة
 */
@Entity
@Table(name = "poster_comments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PosterComment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Poster ID
     * معرف البوستر
     */
    @Column(name = "poster_id", nullable = false)
    private String posterId;

    /**
     * Commenter ID (Admin or Center Manager)
     * معرف المعلق (المدير أو مدير المركز)
     */
    @Column(name = "commenter_id", nullable = false)
    private String commenterId;

    /**
     * Comment Content
     * محتوى التعليق
     */
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * Whether comment is from Admin
     * هل التعليق من المدير
     */
    @Column(name = "is_admin_comment")
    @Builder.Default
    private Boolean isAdminComment = false;

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
        if (isAdminComment == null) isAdminComment = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

