package com.kirkukhealth.poster.repository;

import com.kirkukhealth.poster.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Notification operations
 * مستودع عمليات الإشعارات
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
    
    /**
     * Find unread notifications
     * البحث عن الإشعارات غير المقروءة
     */
    List<Notification> findByReadOrderByCreatedAtDesc(Boolean read);
    
    /**
     * Find all notifications ordered by creation date
     * البحث عن جميع الإشعارات مرتبة حسب تاريخ الإنشاء
     */
    List<Notification> findAllByOrderByCreatedAtDesc();
    
    /**
     * Count unread notifications
     * عدد الإشعارات غير المقروءة
     */
    long countByRead(Boolean read);
}

