package com.kirkukhealth.poster.repository;

import com.kirkukhealth.poster.model.FileUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for File Upload operations
 * مستودع عمليات رفع الملفات
 */
@Repository
public interface FileUploadRepository extends JpaRepository<FileUpload, String> {
    
    /**
     * Find files by category
     * البحث عن الملفات حسب الفئة
     */
    List<FileUpload> findByCategoryOrderByCreatedAtDesc(String category);
    
    /**
     * Find public files
     * البحث عن الملفات العامة
     */
    List<FileUpload> findByIsPublicTrueOrderByCreatedAtDesc();
    
    /**
     * Find files by uploader
     * البحث عن الملفات حسب الرفع
     */
    List<FileUpload> findByUploadedByOrderByCreatedAtDesc(String uploadedBy);
}

