package com.kirkukhealth.poster.controller;

import com.kirkukhealth.poster.model.UserCloudSettings;
import com.kirkukhealth.poster.service.GoogleDriveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Google Drive Controller
 * متحكم Google Drive
 * 
 * Handles OAuth2 flow and Google Drive operations
 * يدير سير عمل OAuth2 وعمليات Google Drive
 */
@RestController
@RequestMapping("/api/drive")
@CrossOrigin(origins = "*")
public class GoogleDriveController {

    @Autowired
    private GoogleDriveService googleDriveService;

    /**
     * Initiate OAuth2 flow
     * بدء سير عمل OAuth2
     * 
     * GET /api/drive/oauth/authorize?userId={userId}
     */
    @GetMapping("/oauth/authorize")
    public ResponseEntity<Map<String, Object>> authorize(@RequestParam String userId) {
        try {
            String state = UUID.randomUUID().toString();
            String authorizationUrl = googleDriveService.getAuthorizationUrl(userId, state);

            Map<String, Object> response = new HashMap<>();
            response.put("authorizationUrl", authorizationUrl);
            response.put("state", state);
            response.put("message", "يرجى زيارة الرابط لإتمام ربط Google Drive");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "خطأ في بدء عملية التفويض");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * OAuth2 callback
     * استدعاء OAuth2
     * 
     * GET /api/drive/oauth/callback?code={code}&state={state}
     */
    @GetMapping("/oauth/callback")
    public ResponseEntity<Map<String, Object>> callback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String error) {
        
        Map<String, Object> response = new HashMap<>();

        if (error != null) {
            response.put("error", "تم رفض التفويض");
            response.put("message", error);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (code == null || state == null) {
            response.put("error", "رمز التفويض أو الحالة مفقودة");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            // Extract userId from state (format: userId:randomState)
            String[] stateParts = state.split(":", 2);
            String userId = stateParts.length > 0 ? stateParts[0] : state;

            UserCloudSettings settings = googleDriveService.exchangeCodeForTokens(userId, code);

            response.put("success", true);
            response.put("message", "تم ربط Google Drive بنجاح");
            response.put("syncStatus", settings.getSyncStatus());
            response.put("folderName", settings.getGoogleDriveFolderName());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "خطأ في ربط Google Drive");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get sync status
     * الحصول على حالة المزامنة
     * 
     * GET /api/drive/status?userId={userId}
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus(@RequestParam String userId) {
        UserCloudSettings settings = googleDriveService.getSyncStatus(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("googleDriveEnabled", settings.getGoogleDriveEnabled());
        response.put("syncStatus", settings.getSyncStatus());
        response.put("folderName", settings.getGoogleDriveFolderName());
        response.put("lastSyncAt", settings.getLastSyncAt());

        return ResponseEntity.ok(response);
    }

    /**
     * Unlink Google Drive
     * إلغاء ربط Google Drive
     * 
     * DELETE /api/drive/unlink?userId={userId}
     */
    @DeleteMapping("/unlink")
    public ResponseEntity<Map<String, Object>> unlink(@RequestParam String userId) {
        googleDriveService.unlinkGoogleDrive(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "تم إلغاء ربط Google Drive بنجاح");

        return ResponseEntity.ok(response);
    }
}

