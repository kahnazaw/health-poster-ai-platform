package com.kirkukhealth.poster.controller;

import com.kirkukhealth.poster.dto.UserProfileRequest;
import com.kirkukhealth.poster.dto.UserProfileResponse;
import com.kirkukhealth.poster.model.UserProfile;
import com.kirkukhealth.poster.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for User Profile Management
 * متحكم REST لإدارة الملف الشخصي للمستخدم
 */
@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    /**
     * Get user profile
     * الحصول على الملف الشخصي
     * 
     * GET /api/profile
     */
    @GetMapping
    public ResponseEntity<UserProfileResponse> getProfile(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        
        if (userId == null || userId.isEmpty()) {
            userId = "default-user-id";
        }
        
        return userProfileService.getProfileByUserId(userId)
            .map(this::mapToResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create or update user profile
     * إنشاء أو تحديث الملف الشخصي
     * 
     * PUT /api/profile
     */
    @PutMapping
    public ResponseEntity<UserProfileResponse> updateProfile(
            @RequestBody UserProfileRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        
        if (userId == null || userId.isEmpty()) {
            userId = "default-user-id";
        }
        
        UserProfile profile = UserProfile.builder()
            .healthCenterName(request.getHealthCenterName())
            .managerName(request.getManagerName())
            .directorateName(request.getDirectorateName())
            .logoPath(request.getLogoPath())
            .showVerificationBadge(request.getShowVerificationBadge())
            .build();
        
        UserProfile updated = userProfileService.updateProfile(userId, profile);
        return ResponseEntity.ok(mapToResponse(updated));
    }

    /**
     * Map UserProfile entity to response DTO
     * تحويل كائن UserProfile إلى كائن الاستجابة
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
}

