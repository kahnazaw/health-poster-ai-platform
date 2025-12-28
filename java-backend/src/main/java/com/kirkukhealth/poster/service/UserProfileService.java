package com.kirkukhealth.poster.service;

import com.kirkukhealth.poster.model.UserProfile;
import com.kirkukhealth.poster.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * User Profile Service
 * خدمة الملف الشخصي للمستخدم
 * 
 * Manages Health Promotion Unit profile settings:
 * - Health Center Name
 * - Manager Name
 * - Directorate Name
 * - Logo path
 * - Verification badge settings
 */
@Service
public class UserProfileService {

    @Autowired
    private UserProfileRepository profileRepository;

    /**
     * Get or create user profile
     * الحصول على أو إنشاء الملف الشخصي للمستخدم
     */
    @Transactional
    public UserProfile getOrCreateProfile(String userId) {
        Optional<UserProfile> existing = profileRepository.findByUserId(userId);
        
        if (existing.isPresent()) {
            return existing.get();
        }
        
        // Create default profile with Kirkuk Health Directorate logo (logo.jpg)
        UserProfile profile = UserProfile.builder()
            .userId(userId)
            .directorateName("دائرة صحة كركوك – قطاع كركوك الأول")
            .logoPath("logo.jpg") // Fixed official logo for all posters
            .showVerificationBadge(true)
            .build();
        
        return profileRepository.save(profile);
    }

    /**
     * Get user profile by user ID
     * الحصول على الملف الشخصي باستخدام معرف المستخدم
     */
    public Optional<UserProfile> getProfileByUserId(String userId) {
        return profileRepository.findByUserId(userId);
    }

    /**
     * Update user profile
     * تحديث الملف الشخصي للمستخدم
     */
    @Transactional
    public UserProfile updateProfile(String userId, UserProfile updatedProfile) {
        UserProfile existing = getOrCreateProfile(userId);
        
        // Update fields
        if (updatedProfile.getHealthCenterName() != null) {
            existing.setHealthCenterName(updatedProfile.getHealthCenterName());
        }
        if (updatedProfile.getManagerName() != null) {
            existing.setManagerName(updatedProfile.getManagerName());
        }
        if (updatedProfile.getDirectorateName() != null) {
            existing.setDirectorateName(updatedProfile.getDirectorateName());
        }
        if (updatedProfile.getLogoPath() != null) {
            existing.setLogoPath(updatedProfile.getLogoPath());
        }
        if (updatedProfile.getShowVerificationBadge() != null) {
            existing.setShowVerificationBadge(updatedProfile.getShowVerificationBadge());
        }
        
        return profileRepository.save(existing);
    }

    /**
     * Save user profile
     * حفظ الملف الشخصي للمستخدم
     */
    @Transactional
    public UserProfile saveProfile(UserProfile profile) {
        return profileRepository.save(profile);
    }

    /**
     * Delete user profile
     * حذف الملف الشخصي للمستخدم
     */
    @Transactional
    public void deleteProfile(String userId) {
        profileRepository.findByUserId(userId)
            .ifPresent(profileRepository::delete);
    }

    /**
     * Increment posters generated count for a health center
     * زيادة عدد البوسترات المولدة للمركز الصحي
     * 
     * This method runs asynchronously to avoid slowing down poster generation
     * تعمل هذه الدالة بشكل غير متزامن لتجنب إبطاء توليد البوسترات
     * 
     * Also updates the last activity timestamp (updated_at)
     * أيضاً تحديث طابع وقت آخر نشاط (updated_at)
     */
    @Transactional
    public void incrementPostersCount(String userId) {
        try {
            Optional<UserProfile> profileOpt = profileRepository.findByUserId(userId);
            if (profileOpt.isPresent()) {
                UserProfile profile = profileOpt.get();
                int currentCount = profile.getPostersGeneratedCount() != null ? profile.getPostersGeneratedCount() : 0;
                profile.setPostersGeneratedCount(currentCount + 1);
                
                // Update last activity timestamp
                profile.setUpdatedAt(java.time.LocalDateTime.now());
                
                profileRepository.save(profile);
                System.out.println("✅ Poster count incremented for center: " + userId + " (Total: " + (currentCount + 1) + ")");
            }
        } catch (Exception e) {
            // Log error but don't throw - tracking should not break poster generation
            System.err.println("⚠️ Failed to increment poster count for " + userId + ": " + e.getMessage());
        }
    }

    /**
     * Get all health centers with their statistics
     * الحصول على جميع المراكز الصحية مع إحصائياتها
     */
    public java.util.List<UserProfile> getAllHealthCentersWithStats() {
        return profileRepository.findAll();
    }
}

