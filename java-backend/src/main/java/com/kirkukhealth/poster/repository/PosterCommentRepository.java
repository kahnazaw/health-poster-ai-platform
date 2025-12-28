package com.kirkukhealth.poster.repository;

import com.kirkukhealth.poster.model.PosterComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Poster Comment operations
 * مستودع عمليات تعليقات البوسترات
 */
@Repository
public interface PosterCommentRepository extends JpaRepository<PosterComment, String> {
    
    /**
     * Find comments for a poster
     * البحث عن التعليقات لبوستر
     */
    List<PosterComment> findByPosterIdOrderByCreatedAtAsc(String posterId);
    
    /**
     * Find admin comments for a poster
     * البحث عن تعليقات المدير لبوستر
     */
    List<PosterComment> findByPosterIdAndIsAdminCommentTrueOrderByCreatedAtAsc(String posterId);
}

