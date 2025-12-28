package com.kirkukhealth.poster.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * User Cloud Settings Entity
 * كيان إعدادات التخزين السحابي للمستخدم
 * 
 * Stores Google Drive OAuth tokens for individual manager linking
 * يخزن رموز OAuth لـ Google Drive لربط كل مدير بشكل فردي
 */
@Entity
@Table(name = "user_cloud_settings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCloudSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * User ID (linked to UserProfile)
     * معرف المستخدم (مرتبط بـ UserProfile)
     */
    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    /**
     * Whether Google Drive is enabled for this user
     * هل Google Drive مفعّل لهذا المستخدم
     */
    @Column(name = "google_drive_enabled")
    @Builder.Default
    private Boolean googleDriveEnabled = false;

    /**
     * Encrypted access token
     * رمز الوصول المشفر
     */
    @Column(name = "access_token_encrypted", columnDefinition = "TEXT")
    private String accessTokenEncrypted;

    /**
     * Encrypted refresh token
     * رمز التحديث المشفر
     */
    @Column(name = "refresh_token_encrypted", columnDefinition = "TEXT")
    private String refreshTokenEncrypted;

    /**
     * Token expiration timestamp
     * وقت انتهاء الرمز
     */
    @Column(name = "token_expires_at")
    private LocalDateTime tokenExpiresAt;

    /**
     * Google Drive folder ID
     * معرف مجلد Google Drive
     */
    @Column(name = "google_drive_folder_id", length = 500)
    private String googleDriveFolderId;

    /**
     * Google Drive folder name
     * اسم مجلد Google Drive
     */
    @Column(name = "google_drive_folder_name", length = 500)
    @Builder.Default
    private String googleDriveFolderName = "My Health Reports - Sector 1";

    /**
     * Last sync timestamp
     * وقت آخر مزامنة
     */
    @Column(name = "last_sync_at")
    private LocalDateTime lastSyncAt;

    /**
     * Sync status: NOT_LINKED, LINKED, SYNCING, SYNCED, ERROR
     * حالة المزامنة
     */
    @Column(name = "sync_status", length = 50)
    @Builder.Default
    private String syncStatus = "NOT_LINKED";

    /**
     * Created timestamp
     * وقت الإنشاء
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Updated timestamp
     * وقت آخر تحديث
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (googleDriveEnabled == null) googleDriveEnabled = false;
        if (syncStatus == null) syncStatus = "NOT_LINKED";
        if (googleDriveFolderName == null) googleDriveFolderName = "My Health Reports - Sector 1";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Sync Status Enum
     */
    public enum SyncStatus {
        NOT_LINKED, LINKED, SYNCING, SYNCED, ERROR
    }
}

