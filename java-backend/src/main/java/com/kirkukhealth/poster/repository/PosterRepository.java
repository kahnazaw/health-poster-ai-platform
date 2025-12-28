package com.kirkukhealth.poster.repository;

import com.kirkukhealth.poster.model.Poster;
import com.kirkukhealth.poster.model.Poster.PosterStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Poster operations
 * مستودع عمليات البوسترات
 */
@Repository
public interface PosterRepository extends JpaRepository<Poster, String> {
    
    /**
     * Find posters by status
     * البحث عن البوسترات حسب الحالة
     */
    List<Poster> findByStatus(PosterStatus status);
    
    /**
     * Find posters by user ID
     * البحث عن البوسترات حسب معرف المستخدم
     */
    List<Poster> findByUserId(String userId);
    
    /**
     * Find posters by user ID and status
     * البحث عن البوسترات حسب معرف المستخدم والحالة
     */
    List<Poster> findByUserIdAndStatus(String userId, PosterStatus status);
    
    /**
     * Find pending posters (waiting for approval)
     * البحث عن البوسترات في الانتظار (تنتظر الموافقة)
     */
    List<Poster> findByStatusOrderByCreatedAtDesc(PosterStatus status);
    
    /**
     * Count pending posters
     * عدد البوسترات في الانتظار
     */
    long countByStatus(PosterStatus status);
}

