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
 * 1. AI-powered content generation (Gemini AI)
 * 2. Content validation against MOH guidelines
 * 3. Cultural context enhancement
 * 4. Image generation with logo and footer
 */
@Service
public class PosterGenerationService {

    @Autowired
    private ContentAuthorityService contentAuthorityService;
    
    @Autowired
    private PosterImageService posterImageService;
    
    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private GeminiAIService geminiAIService;

    /**
     * Generate complete poster (content + image)
     * توليد البوستر الكامل (المحتوى + الصورة)
     * 
     * If content is not AI-generated and topic is provided, uses Gemini AI to generate content
     */
    public PosterGenerationResult generatePoster(PosterContent content, String userId) throws IOException {
        // Get or create user profile
        UserProfile profile = userProfileService.getOrCreateProfile(userId);
        
        // If content is not AI-generated and topic is provided, generate with AI
        if ((content.getAiGenerated() == null || !content.getAiGenerated()) 
            && content.getTopic() != null && !content.getTopic().isEmpty()) {
            try {
                // Determine tone based on topic category
                String tone = determineToneForTopic(content.getTopic());
                String language = content.getLanguage() != null ? content.getLanguage() : "ar";
                
                // Generate AI content
                PosterContent aiContent = geminiAIService.generatePosterContent(
                    content.getTopic(), 
                    language, 
                    tone
                );
                
                // Merge AI content with user-provided content (if any)
                if (content.getTitle() != null && !content.getTitle().isEmpty()) {
                    aiContent.setTitle(content.getTitle());
                }
                if (content.getMainMessage() != null && !content.getMainMessage().isEmpty()) {
                    aiContent.setMainMessage(content.getMainMessage());
                }
                if (content.getBulletPoints() != null && !content.getBulletPoints().isEmpty()) {
                    // Merge user bullet points with AI-generated ones
                    aiContent.getBulletPoints().addAll(0, content.getBulletPoints());
                }
                
                content = aiContent;
                System.out.println("✅ AI content generated for topic: " + content.getTopic());
            } catch (Exception e) {
                System.err.println("⚠️ AI generation failed, using provided content: " + e.getMessage());
                // Continue with provided content
            }
        }
        
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
     * Determine appropriate tone for a topic
     * تحديد النبرة المناسبة لموضوع
     */
    private String determineToneForTopic(String topic) {
        if (topic == null) return "formal";
        
        String lowerTopic = topic.toLowerCase();
        if (lowerTopic.contains("كوليرا") || lowerTopic.contains("covid") || 
            lowerTopic.contains("طوارئ") || lowerTopic.contains("معدية")) {
            return "emergency";
        } else if (lowerTopic.contains("تغذية") || lowerTopic.contains("نظافة") || 
                   lowerTopic.contains("تطعيم") || lowerTopic.contains("تعليم")) {
            return "educational";
        } else {
            return "formal";
        }
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

