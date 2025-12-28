package com.kirkukhealth.poster.controller;

import com.kirkukhealth.poster.dto.PosterGenerationRequest;
import com.kirkukhealth.poster.dto.PosterGenerationResponse;
import com.kirkukhealth.poster.model.PosterContent;
import com.kirkukhealth.poster.model.UserProfile;
import com.kirkukhealth.poster.service.PosterGenerationService;
import com.kirkukhealth.poster.service.PosterExportService;
import com.kirkukhealth.poster.service.PosterImageService;
import com.kirkukhealth.poster.service.UserProfileService;
import com.kirkukhealth.poster.service.PosterGovernanceService;
import com.kirkukhealth.poster.model.Poster;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for Poster Generation
 * متحكم REST لتوليد البوسترات
 */
@RestController
@RequestMapping("/api/posters")
@CrossOrigin(origins = "*")
public class PosterController {

    @Autowired
    private PosterGenerationService posterGenerationService;
    
    @Autowired
    private PosterExportService posterExportService;
    
    @Autowired
    private PosterImageService posterImageService;
    
    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private PosterGovernanceService governanceService;

    /**
     * Generate poster
     * توليد بوستر
     * 
     * POST /api/posters/generate
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generatePoster(
            @Valid @RequestBody PosterGenerationRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        
        try {
            // Default userId for testing (should come from authentication in production)
            if (userId == null || userId.isEmpty()) {
                userId = "default-user-id";
            }
            
            // Convert request to PosterContent
            PosterContent content = PosterContent.builder()
                .title(request.getTitle())
                .topic(request.getTopic())
                .mainMessage(request.getMainMessage())
                .bulletPoints(request.getBulletPoints())
                .closing(request.getClosing())
                .language(request.getLanguage() != null ? request.getLanguage() : "ar")
                .targetAudience(request.getTargetAudience())
                .tone(request.getTone())
                .aiGenerated(request.getUseAI() != null && request.getUseAI())
                .mohApproved(request.getUseMOHGuidelines() != null && request.getUseMOHGuidelines())
                .build();
            
            // Generate poster
            PosterGenerationService.PosterGenerationResult result = 
                posterGenerationService.generatePoster(content, userId);
            
            // Create poster entity with DRAFT status (includes logo.jpg in imageBytes)
            Poster poster = governanceService.createPoster(content, userId, result.getImageBytes());
            
            // Track poster generation (async - doesn't slow down response)
            // تتبع توليد البوستر (غير متزامن - لا يبطئ الاستجابة)
            try {
                userProfileService.incrementPostersCount(userId);
            } catch (Exception e) {
                // Log but don't fail - tracking is non-critical
                System.err.println("⚠️ Failed to track poster generation: " + e.getMessage());
            }
            
            // Convert image to base64 for response
            String imageBase64 = Base64.getEncoder().encodeToString(result.getImageBytes());
            
            // Build response
            PosterGenerationResponse response = PosterGenerationResponse.builder()
                .posterId(poster.getId())
                .imageUrl("data:image/png;base64," + imageBase64)
                .content(PosterGenerationResponse.PosterContentResponse.builder()
                    .title(result.getContent().getTitle())
                    .topic(result.getContent().getTopic())
                    .mainMessage(result.getContent().getMainMessage())
                    .bulletPoints(result.getContent().getBulletPoints())
                    .closing(result.getContent().getClosing())
                    .language(result.getContent().getLanguage())
                    .build())
                .mohApproved(result.getMohApproved())
                .verificationBadge(result.getProfile().getShowVerificationBadge())
                .message("تم توليد البوستر بنجاح - الحالة: " + poster.getStatus().name())
                .build();
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("خطأ في المحتوى", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("خطأ في توليد الصورة", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("خطأ في الخادم", e.getMessage()));
        }
    }

    /**
     * Submit poster for approval (DRAFT -> PENDING)
     * إرسال البوستر للموافقة (مسودة -> في الانتظار)
     * 
     * POST /api/posters/{posterId}/submit
     */
    @PostMapping("/{posterId}/submit")
    public ResponseEntity<Map<String, Object>> submitForApproval(
            @PathVariable String posterId,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        
        try {
            if (userId == null || userId.isEmpty()) {
                userId = "default-user-id";
            }

            Poster poster = governanceService.submitForApproval(posterId, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("posterId", poster.getId());
            response.put("status", poster.getStatus().name());
            response.put("message", "تم إرسال البوستر للموافقة بنجاح");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "خطأ في الإرسال");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "خطأ في الخادم");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Download approved poster (300 DPI) - Only for APPROVED posters
     * تحميل البوستر المعتمد (300 DPI) - فقط للبوسترات المعتمدة
     * 
     * GET /api/posters/{posterId}/download
     */
    @GetMapping("/{posterId}/download")
    public ResponseEntity<byte[]> downloadPoster(@PathVariable String posterId) {
        try {
            byte[] imageBytes = governanceService.downloadApprovedPoster(posterId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(imageBytes.length);
            headers.setContentDispositionFormData("attachment", "poster-" + posterId + ".png");

            return ResponseEntity.ok()
                .headers(headers)
                .body(imageBytes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage().getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(("Error: " + e.getMessage()).getBytes());
        }
    }
    
    /**
     * Generate Welcome Poster for Health Center
     * توليد بوستر ترحيبي للمركز الصحي
     * 
     * GET /api/posters/welcome/{centerId}
     * 
     * Features:
     * - Automatically fetches manager_name and health_center_name from user_profiles table
     * - Header: logo.jpg (Kirkuk Health Directorate)
     * - Body: Welcome message in Arabic/Turkmen
     * - Footer: Manager Name and Health Center Name
     * - 300 DPI print quality for "AZAW TEAM CENTER"
     * - Returns image directly for immediate viewing
     */
    @GetMapping("/welcome/{centerId}")
    public ResponseEntity<byte[]> generateWelcomePoster(@PathVariable String centerId) {
        try {
            // Fetch user profile by centerId (which is userId in user_profiles table)
            Optional<UserProfile> profileOpt = userProfileService.getProfileByUserId(centerId);
            
            if (profileOpt.isEmpty()) {
                // Return 404 if center not found
                return ResponseEntity.notFound().build();
            }
            
            UserProfile profile = profileOpt.get();
            
            // Verify required data exists
            if (profile.getHealthCenterName() == null || profile.getHealthCenterName().isEmpty() ||
                profile.getManagerName() == null || profile.getManagerName().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Missing required data: health_center_name or manager_name".getBytes());
            }
            
            // Generate welcome poster (300 DPI)
            byte[] imageBytes = posterImageService.generateWelcomePoster(profile);
            
            // Track welcome poster generation (async - doesn't slow down response)
            // تتبع توليد بوستر الترحيب (غير متزامن - لا يبطئ الاستجابة)
            try {
                userProfileService.incrementPostersCount(centerId);
            } catch (Exception e) {
                // Log but don't fail - tracking is non-critical
                System.err.println("⚠️ Failed to track welcome poster generation: " + e.getMessage());
            }
            
            // Return image directly with proper headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(imageBytes.length);
            headers.set("Content-Disposition", "inline; filename=\"welcome-poster-" + centerId + ".png\"");
            
            System.out.println("✅ Welcome poster generated for center: " + centerId);
            System.out.println("   - Health Center: " + profile.getHealthCenterName());
            System.out.println("   - Manager: " + profile.getManagerName());
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(imageBytes);
            
        } catch (IOException e) {
            System.err.println("❌ Error generating welcome poster: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(("Error generating welcome poster: " + e.getMessage()).getBytes());
        } catch (Exception e) {
            System.err.println("❌ Unexpected error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(("Unexpected error: " + e.getMessage()).getBytes());
        }
    }
    
    /**
     * Get poster image metadata (resolution, DPI, etc.)
     * الحصول على معلومات صورة البوستر (الدقة، DPI، إلخ)
     * 
     * GET /api/posters/metadata
     */
    @GetMapping("/metadata")
    public ResponseEntity<PosterExportService.ImageMetadata> getPosterMetadata() {
        PosterExportService.ImageMetadata metadata = posterExportService.getImageMetadata();
        return ResponseEntity.ok(metadata);
    }

    /**
     * Error response class
     * Used for JSON serialization in error responses
     */
    public static class ErrorResponse {
        @lombok.Getter
        private String error;
        
        @lombok.Getter
        private String message;
        
        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }
    }
}

