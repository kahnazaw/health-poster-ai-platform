package com.kirkukhealth.poster.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for user profile
 * كائن استجابة الملف الشخصي
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("healthCenterName")
    private String healthCenterName;

    @JsonProperty("managerName")
    private String managerName;

    @JsonProperty("directorateName")
    private String directorateName;

    @JsonProperty("logoPath")
    private String logoPath;

    @JsonProperty("showVerificationBadge")
    private Boolean showVerificationBadge;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
}

