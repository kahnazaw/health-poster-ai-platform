package com.kirkukhealth.poster.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for user profile update
 * كائن طلب تحديث الملف الشخصي
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileRequest {

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
}

