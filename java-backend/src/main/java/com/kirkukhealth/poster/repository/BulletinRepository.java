package com.kirkukhealth.poster.repository;

import com.kirkukhealth.poster.model.Bulletin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Bulletin operations
 * مستودع عمليات النشرات الرسمية
 */
@Repository
public interface BulletinRepository extends JpaRepository<Bulletin, String> {
    
    /**
     * Find all bulletins ordered by pinned first, then by creation date
     * البحث عن جميع النشرات مرتبة حسب المثبت أولاً، ثم تاريخ الإنشاء
     */
    List<Bulletin> findAllByOrderByIsPinnedDescCreatedAtDesc();
    
    /**
     * Find pinned bulletins
     * البحث عن النشرات المثبتة
     */
    List<Bulletin> findByIsPinnedTrueOrderByCreatedAtDesc();
    
    /**
     * Find bulletins by priority
     * البحث عن النشرات حسب الأولوية
     */
    List<Bulletin> findByPriorityOrderByCreatedAtDesc(String priority);
}

