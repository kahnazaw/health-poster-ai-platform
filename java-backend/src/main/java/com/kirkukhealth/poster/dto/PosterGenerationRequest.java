package com.kirkukhealth.poster.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * Request DTO for poster generation
 * كائن طلب توليد البوستر
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PosterGenerationRequest {

    @NotBlank(message = "الموضوع مطلوب")
    @JsonProperty("topic")
    private String topic;

    @JsonProperty("title")
    private String title;

    @JsonProperty("mainMessage")
    private String mainMessage;

    @JsonProperty("bulletPoints")
    private List<String> bulletPoints;

    @JsonProperty("closing")
    private String closing;

    @JsonProperty("language")
    private String language; // ar, ku, tr, en

    @JsonProperty("targetAudience")
    private String targetAudience;

    @JsonProperty("tone")
    private String tone; // formal, friendly

    @JsonProperty("useAI")
    private Boolean useAI;

    @JsonProperty("useMOHGuidelines")
    private Boolean useMOHGuidelines; // Whether to enforce MOH guidelines
}

