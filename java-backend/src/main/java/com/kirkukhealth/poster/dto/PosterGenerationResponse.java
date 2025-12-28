package com.kirkukhealth.poster.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for poster generation
 * كائن استجابة توليد البوستر
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PosterGenerationResponse {

    @JsonProperty("posterId")
    private String posterId;

    @JsonProperty("imageUrl")
    private String imageUrl; // URL to access the generated poster image

    @JsonProperty("content")
    private PosterContentResponse content;

    @JsonProperty("mohApproved")
    private Boolean mohApproved;

    @JsonProperty("verificationBadge")
    private Boolean verificationBadge;

    @JsonProperty("message")
    private String message;

    /**
     * Nested content response
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PosterContentResponse {
        @JsonProperty("title")
        private String title;

        @JsonProperty("topic")
        private String topic;

        @JsonProperty("mainMessage")
        private String mainMessage;

        @JsonProperty("bulletPoints")
        private java.util.List<String> bulletPoints;

        @JsonProperty("closing")
        private String closing;

        @JsonProperty("language")
        private String language;
    }
}

