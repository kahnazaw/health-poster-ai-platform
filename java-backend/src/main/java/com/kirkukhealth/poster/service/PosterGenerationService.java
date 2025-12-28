package com.kirkukhealth.poster.service;

import com.kirkukhealth.poster.model.PosterContent;
import com.kirkukhealth.poster.model.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Poster Generation Service
 * خدمة توليد البوسترات
 * 
 * Orchestrates the poster generation process:
 * 1. Content validation against MOH guidelines
 * 2. Cultural context enhancement
 * 3. Image generation with logo and footer
 */
@Service
public class PosterGenerationService {

    @Autowired
    private ContentAuthorityService contentAuthorityService;
    
    @Autowired
    private PosterImageService posterImageService;
    
    @Autowired
    private UserProfileService userProfileService;

    /**
     * Generate complete poster (content + image)
     * توليد البوستر الكامل (المحتوى + الصورة)
     */
    public PosterGenerationResult generatePoster(PosterContent content, String userId) throws IOException {
        // Get or create user profile
        UserProfile profile = userProfileService.getOrCreateProfile(userId);
        
        // Enhance content with MOH guidelines
        content = contentAuthorityService.enhanceWithMOHGuidelines(content);
        
        // Add cultural context for Kirkuk/Iraq
        content = contentAuthorityService.addCulturalContext(content);
        
        // Validate content
        boolean isValid = contentAuthorityService.validateContent(content);
        if (!isValid) {
            throw new IllegalArgumentException("Content does not meet MOH guidelines");
        }
        
        // Generate poster image
        byte[] imageBytes = posterImageService.generatePosterImage(content, profile);
        
        return PosterGenerationResult.builder()
            .content(content)
            .imageBytes(imageBytes)
            .profile(profile)
            .mohApproved(content.getMohApproved())
            .build();
    }

    /**
     * Result class for poster generation
     */
    public static class PosterGenerationResult {
        private PosterContent content;
        private byte[] imageBytes;
        private UserProfile profile;
        private Boolean mohApproved;
        
        // Builder pattern
        public static PosterGenerationResultBuilder builder() {
            return new PosterGenerationResultBuilder();
        }
        
        public static class PosterGenerationResultBuilder {
            private PosterContent content;
            private byte[] imageBytes;
            private UserProfile profile;
            private Boolean mohApproved;
            
            public PosterGenerationResultBuilder content(PosterContent content) {
                this.content = content;
                return this;
            }
            
            public PosterGenerationResultBuilder imageBytes(byte[] imageBytes) {
                this.imageBytes = imageBytes;
                return this;
            }
            
            public PosterGenerationResultBuilder profile(UserProfile profile) {
                this.profile = profile;
                return this;
            }
            
            public PosterGenerationResultBuilder mohApproved(Boolean mohApproved) {
                this.mohApproved = mohApproved;
                return this;
            }
            
            public PosterGenerationResult build() {
                PosterGenerationResult result = new PosterGenerationResult();
                result.content = this.content;
                result.imageBytes = this.imageBytes;
                result.profile = this.profile;
                result.mohApproved = this.mohApproved;
                return result;
            }
        }
        
        // Getters
        public PosterContent getContent() { return content; }
        public byte[] getImageBytes() { return imageBytes; }
        public UserProfile getProfile() { return profile; }
        public Boolean getMohApproved() { return mohApproved; }
    }
}

