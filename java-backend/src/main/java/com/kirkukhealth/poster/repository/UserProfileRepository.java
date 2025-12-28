package com.kirkukhealth.poster.repository;

import com.kirkukhealth.poster.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User Profile operations
 * مستودع عمليات الملف الشخصي للمستخدم
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, String> {
    
    /**
     * Find user profile by user ID
     * البحث عن الملف الشخصي باستخدام معرف المستخدم
     */
    Optional<UserProfile> findByUserId(String userId);
    
    /**
     * Check if profile exists for user
     * التحقق من وجود ملف شخصي للمستخدم
     */
    boolean existsByUserId(String userId);
}

