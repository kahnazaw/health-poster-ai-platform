package com.kirkukhealth.poster.repository;

import com.kirkukhealth.poster.model.UserCloudSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User Cloud Settings operations
 * مستودع عمليات إعدادات التخزين السحابي للمستخدمين
 */
@Repository
public interface UserCloudSettingsRepository extends JpaRepository<UserCloudSettings, String> {
    
    /**
     * Find settings by user ID
     * البحث عن الإعدادات حسب معرف المستخدم
     */
    Optional<UserCloudSettings> findByUserId(String userId);
    
    /**
     * Check if user has Google Drive enabled
     * التحقق من تفعيل Google Drive للمستخدم
     */
    boolean existsByUserIdAndGoogleDriveEnabledTrue(String userId);
}

