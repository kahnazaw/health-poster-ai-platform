package com.kirkukhealth.poster.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * File Upload Entity
 * كيان رفع الملفات
 * 
 * Section to upload/download high-res logos and official documents
 * قسم لرفع/تحميل الشعارات عالية الدقة والوثائق الرسمية
 */
@Entity
@Table(name = "file_uploads")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUpload {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * File name (stored name)
     * اسم الملف (الاسم المحفوظ)
     */
    @Column(name = "file_name", nullable = false, length = 500)
    private String fileName;

    /**
     * Original file name
     * اسم الملف الأصلي
     */
    @Column(name = "original_name", nullable = false, length = 500)
    private String originalName;

    /**
     * File path (storage location)
     * مسار الملف (موقع التخزين)
     */
    @Column(name = "file_path", nullable = false, length = 1000)
    private String filePath;

    /**
     * File size in bytes
     * حجم الملف بالبايت
     */
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    /**
     * MIME type
     * نوع MIME
     */
    @Column(name = "mime_type", length = 100)
    private String mimeType;

    /**
     * User ID who uploaded the file
     * معرف المستخدم الذي رفع الملف
     */
    @Column(name = "uploaded_by", nullable = false)
    private String uploadedBy;

    /**
     * Category: LOGO, DOCUMENT, TEMPLATE, OTHER
     * الفئة: شعار، وثيقة، قالب، أخرى
     */
    @Column(name = "category", length = 100)
    @Builder.Default
    private String category = "GENERAL";

    /**
     * File description
     * وصف الملف
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Whether file is public (accessible to all centers)
     * هل الملف عام (يمكن لجميع المراكز الوصول إليه)
     */
    @Column(name = "is_public")
    @Builder.Default
    private Boolean isPublic = true;

    /**
     * Download count
     * عدد التحميلات
     */
    @Column(name = "download_count")
    @Builder.Default
    private Integer downloadCount = 0;

    /**
     * Created timestamp
     * وقت الإنشاء
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (category == null) category = "GENERAL";
        if (isPublic == null) isPublic = true;
        if (downloadCount == null) downloadCount = 0;
    }

    /**
     * File Category Enum
     */
    public enum FileCategory {
        LOGO, DOCUMENT, TEMPLATE, OTHER, GENERAL
    }
}

