package com.kirkukhealth.poster.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Poster Content Structure
 * هيكل محتوى البوستر
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PosterContent {

    @JsonProperty("title")
    private String title;

    @JsonProperty("topic")
    private String topic;

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

    @JsonProperty("aiGenerated")
    private Boolean aiGenerated;

    @JsonProperty("mohApproved")
    private Boolean mohApproved; // Whether content follows Iraqi MOH guidelines
}

