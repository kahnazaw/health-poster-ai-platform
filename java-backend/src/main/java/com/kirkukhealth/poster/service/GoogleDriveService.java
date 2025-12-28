package com.kirkukhealth.poster.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.kirkukhealth.poster.model.UserCloudSettings;
import com.kirkukhealth.poster.repository.UserCloudSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Google Drive Service
 * خدمة Google Drive
 * 
 * Handles OAuth2 flow and file uploads to individual manager Google Drive accounts
 * يدير سير عمل OAuth2 ورفع الملفات إلى حسابات Google Drive لكل مدير بشكل فردي
 */
@Service
public class GoogleDriveService {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);
    private static final String APPLICATION_NAME = "Kirkuk Health Directorate - Sector 1";

    @Value("${google.oauth.client.id:}")
    private String clientId;

    @Value("${google.oauth.client.secret:}")
    private String clientSecret;

    @Value("${google.oauth.redirect.uri:http://localhost:8080/api/drive/oauth/callback}")
    private String redirectUri;

    @Autowired
    private UserCloudSettingsRepository cloudSettingsRepository;

    @Autowired
    private TokenEncryptionService encryptionService;

    /**
     * Get authorization URL for OAuth2 flow
     * الحصول على رابط التفويض لسير عمل OAuth2
     */
    public String getAuthorizationUrl(String userId, String state) throws IOException, GeneralSecurityException {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
            httpTransport, JSON_FACTORY, clientId, clientSecret, SCOPES)
            .setAccessType("offline")
            .setApprovalPrompt("force")
            .build();

        return flow.newAuthorizationUrl()
            .setRedirectUri(redirectUri)
            .setState(userId + ":" + state)
            .build();
    }

    /**
     * Exchange authorization code for tokens
     * استبدال رمز التفويض بالرموز
     */
    @Transactional
    public UserCloudSettings exchangeCodeForTokens(String userId, String authorizationCode) 
            throws IOException, GeneralSecurityException {
        
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
            httpTransport, JSON_FACTORY, clientId, clientSecret, SCOPES)
            .setAccessType("offline")
            .setApprovalPrompt("force")
            .build();

        com.google.api.client.auth.oauth2.TokenResponse tokenResponse = flow
            .newTokenRequest(authorizationCode)
            .setRedirectUri(redirectUri)
            .execute();

        // Get or create user cloud settings
        UserCloudSettings settings = cloudSettingsRepository.findByUserId(userId)
            .orElse(UserCloudSettings.builder()
                .userId(userId)
                .googleDriveEnabled(false)
                .build());

        // Encrypt and store tokens
        settings.setAccessTokenEncrypted(encryptionService.encrypt(tokenResponse.getAccessToken()));
        settings.setRefreshTokenEncrypted(encryptionService.encrypt(tokenResponse.getRefreshToken()));
        settings.setGoogleDriveEnabled(true);
        settings.setSyncStatus("LINKED");
        settings.setTokenExpiresAt(LocalDateTime.now().plusSeconds(tokenResponse.getExpiresInSeconds()));

        // Create folder in user's Drive
        String folderId = createOrGetFolder(userId, settings);
        settings.setGoogleDriveFolderId(folderId);

        return cloudSettingsRepository.save(settings);
    }

    /**
     * Create or get folder in user's Google Drive
     * إنشاء أو الحصول على مجلد في Google Drive للمستخدم
     */
    private String createOrGetFolder(String userId, UserCloudSettings settings) 
            throws IOException, GeneralSecurityException {
        
        Drive driveService = getDriveService(userId);
        if (driveService == null) {
            return null;
        }

        String folderName = settings.getGoogleDriveFolderName();
        
        // Search for existing folder
        FileList result = driveService.files().list()
            .setQ("name='" + folderName + "' and mimeType='application/vnd.google-apps.folder' and trashed=false")
            .setSpaces("drive")
            .setFields("files(id, name)")
            .execute();

        if (result.getFiles() != null && !result.getFiles().isEmpty()) {
            return result.getFiles().get(0).getId();
        }

        // Create new folder
        File folderMetadata = new File();
        folderMetadata.setName(folderName);
        folderMetadata.setMimeType("application/vnd.google-apps.folder");

        File folder = driveService.files().create(folderMetadata)
            .setFields("id")
            .execute();

        return folder.getId();
    }

    /**
     * Upload PDF to user's Google Drive
     * رفع PDF إلى Google Drive للمستخدم
     */
    @Transactional
    public String uploadPdfToDrive(String userId, byte[] pdfBytes, String fileName) 
            throws IOException, GeneralSecurityException {
        
        UserCloudSettings settings = cloudSettingsRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalStateException("Google Drive not linked for user: " + userId));

        if (!settings.getGoogleDriveEnabled() || settings.getGoogleDriveFolderId() == null) {
            throw new IllegalStateException("Google Drive not properly configured for user: " + userId);
        }

        Drive driveService = getDriveService(userId);
        if (driveService == null) {
            throw new IllegalStateException("Failed to initialize Google Drive service for user: " + userId);
        }

        // Create file metadata
        File fileMetadata = new File();
        fileMetadata.setName(fileName);
        fileMetadata.setParents(Collections.singletonList(settings.getGoogleDriveFolderId()));

        // Upload file - create temp file first
        java.io.File tempFile = java.io.File.createTempFile("report_", ".pdf");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(pdfBytes);
        }
        
        com.google.api.client.http.FileContent mediaContent = 
            new com.google.api.client.http.FileContent("application/pdf", tempFile);
        
        File file = driveService.files().create(fileMetadata, mediaContent)
            .setFields("id, name, webViewLink")
            .execute();
        
        // Delete temp file
        tempFile.delete();

        // Update sync status
        settings.setLastSyncAt(LocalDateTime.now());
        settings.setSyncStatus("SYNCED");
        cloudSettingsRepository.save(settings);

        return file.getId();
    }

    /**
     * Get Drive service for a specific user
     * الحصول على خدمة Drive لمستخدم محدد
     */
    private Drive getDriveService(String userId) throws IOException, GeneralSecurityException {
        UserCloudSettings settings = cloudSettingsRepository.findByUserId(userId)
            .orElse(null);

        if (settings == null || !settings.getGoogleDriveEnabled()) {
            return null;
        }

        // Check if token needs refresh (before decrypting)
        refreshTokenIfNeeded(userId, settings);
        
        // Reload settings after potential refresh
        settings = cloudSettingsRepository.findByUserId(userId).orElse(settings);
        
        // Decrypt tokens
        String accessToken = encryptionService.decrypt(settings.getAccessTokenEncrypted());
        String refreshToken = encryptionService.decrypt(settings.getRefreshTokenEncrypted());

        if (accessToken == null) {
            return null;
        }

        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        
        Credential credential = new Credential.Builder(
            com.google.api.client.auth.oauth2.BearerToken.authorizationHeaderAccessMethod())
            .setTransport(httpTransport)
            .setJsonFactory(JSON_FACTORY)
            .setTokenServerUrl(new GenericUrl("https://oauth2.googleapis.com/token"))
            .setClientAuthentication(new com.google.api.client.auth.oauth2.ClientParametersAuthentication(
                clientId, clientSecret))
            .build();
        
        credential.setAccessToken(accessToken);
        if (refreshToken != null) {
            credential.setRefreshToken(refreshToken);
        }

        return new Drive.Builder(httpTransport, JSON_FACTORY, credential)
            .setApplicationName(APPLICATION_NAME)
            .build();
    }

    /**
     * Unlink Google Drive for a user
     * إلغاء ربط Google Drive لمستخدم
     */
    @Transactional
    public void unlinkGoogleDrive(String userId) {
        cloudSettingsRepository.findByUserId(userId).ifPresent(settings -> {
            settings.setGoogleDriveEnabled(false);
            settings.setAccessTokenEncrypted(null);
            settings.setRefreshTokenEncrypted(null);
            settings.setTokenExpiresAt(null);
            settings.setGoogleDriveFolderId(null);
            settings.setSyncStatus("NOT_LINKED");
            cloudSettingsRepository.save(settings);
        });
    }

    /**
     * Get sync status for a user
     * الحصول على حالة المزامنة لمستخدم
     */
    public UserCloudSettings getSyncStatus(String userId) {
        return cloudSettingsRepository.findByUserId(userId)
            .orElse(UserCloudSettings.builder()
                .userId(userId)
                .googleDriveEnabled(false)
                .syncStatus("NOT_LINKED")
                .build());
    }

    /**
     * Refresh access token if expired
     * تحديث رمز الوصول إذا انتهت صلاحيته
     */
    private void refreshTokenIfNeeded(String userId, UserCloudSettings settings) 
            throws IOException, GeneralSecurityException {
        
        if (settings.getTokenExpiresAt() == null || 
            LocalDateTime.now().isBefore(settings.getTokenExpiresAt().minusMinutes(5))) {
            return; // Token still valid
        }

        // Token expired or about to expire, refresh it
        String refreshToken = encryptionService.decrypt(settings.getRefreshTokenEncrypted());
        if (refreshToken == null) {
            throw new IllegalStateException("Refresh token not available for user: " + userId);
        }

        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        
        com.google.api.client.auth.oauth2.TokenResponse tokenResponse = 
            new com.google.api.client.auth.oauth2.RefreshTokenRequest(
                httpTransport, JSON_FACTORY,
                new GenericUrl("https://oauth2.googleapis.com/token"),
                refreshToken)
            .setClientAuthentication(new com.google.api.client.auth.oauth2.ClientParametersAuthentication(clientId, clientSecret))
            .execute();

        // Update tokens
        settings.setAccessTokenEncrypted(encryptionService.encrypt(tokenResponse.getAccessToken()));
        if (tokenResponse.getRefreshToken() != null) {
            settings.setRefreshTokenEncrypted(encryptionService.encrypt(tokenResponse.getRefreshToken()));
        }
        settings.setTokenExpiresAt(LocalDateTime.now().plusSeconds(tokenResponse.getExpiresInSeconds()));
        cloudSettingsRepository.save(settings);
    }
}

