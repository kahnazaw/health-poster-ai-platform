package com.kirkukhealth.poster.controller;

import com.kirkukhealth.poster.dto.UserProfileResponse;
import com.kirkukhealth.poster.model.UserProfile;
import com.kirkukhealth.poster.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for Health Center Management (23 Centers)
 * متحكم REST لإدارة المراكز الصحية (23 مركز)
 */
@RestController
@RequestMapping("/api/health-centers")
@CrossOrigin(origins = "*")
public class HealthCenterController {

    @Autowired
    private UserProfileService userProfileService;

    /**
     * Get all health centers
     * الحصول على جميع المراكز الصحية
     * 
     * GET /api/health-centers
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllHealthCenters() {
        // This would fetch from database - for now return structure
        Map<String, Object> response = new HashMap<>();
        response.put("totalCenters", 23);
        response.put("message", "23 مراكز صحية لدائرة صحة كركوك – قطاع كركوك الأول");
        return ResponseEntity.ok(response);
    }

    /**
     * Create or update health center profile
     * إنشاء أو تحديث ملف مركز صحي
     * 
     * POST /api/health-centers
     */
    @PostMapping
    public ResponseEntity<UserProfileResponse> createHealthCenter(
            @RequestBody HealthCenterRequest request) {
        
        UserProfile profile = UserProfile.builder()
            .userId(request.getUserId())
            .healthCenterName(request.getHealthCenterName())
            .managerName(request.getManagerName())
            .directorateName("دائرة صحة كركوك – قطاع كركوك الأول")
            .logoPath("logo.jpg") // Fixed official logo
            .showVerificationBadge(true)
            .build();
        
        UserProfile saved = userProfileService.saveProfile(profile);
        return ResponseEntity.ok(mapToResponse(saved));
    }

    /**
     * Update health center by user ID
     * تحديث مركز صحي باستخدام معرف المستخدم
     * 
     * PUT /api/health-centers/{userId}
     */
    @PutMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> updateHealthCenter(
            @PathVariable String userId,
            @RequestBody HealthCenterRequest request) {
        
        UserProfile profile = UserProfile.builder()
            .healthCenterName(request.getHealthCenterName())
            .managerName(request.getManagerName())
            .directorateName("دائرة صحة كركوك – قطاع كركوك الأول")
            .logoPath("logo.jpg")
            .showVerificationBadge(true)
            .build();
        
        UserProfile updated = userProfileService.updateProfile(userId, profile);
        return ResponseEntity.ok(mapToResponse(updated));
    }

    /**
     * Get health center by user ID
     * الحصول على مركز صحي باستخدام معرف المستخدم
     * 
     * GET /api/health-centers/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> getHealthCenter(@PathVariable String userId) {
        return userProfileService.getProfileByUserId(userId)
            .map(this::mapToResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Map UserProfile to Response DTO
     */
    private UserProfileResponse mapToResponse(UserProfile profile) {
        return UserProfileResponse.builder()
            .id(profile.getId())
            .userId(profile.getUserId())
            .healthCenterName(profile.getHealthCenterName())
            .managerName(profile.getManagerName())
            .directorateName(profile.getDirectorateName())
            .logoPath(profile.getLogoPath())
            .showVerificationBadge(profile.getShowVerificationBadge())
            .createdAt(profile.getCreatedAt())
            .updatedAt(profile.getUpdatedAt())
            .build();
    }

    /**
     * Health Center Request DTO
     */
    public static class HealthCenterRequest {
        private String userId;
        private String healthCenterName;
        private String managerName;

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getHealthCenterName() { return healthCenterName; }
        public void setHealthCenterName(String healthCenterName) { this.healthCenterName = healthCenterName; }
        public String getManagerName() { return managerName; }
        public void setManagerName(String managerName) { this.managerName = managerName; }
    }
}

