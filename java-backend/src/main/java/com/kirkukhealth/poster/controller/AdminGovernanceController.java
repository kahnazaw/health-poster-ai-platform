package com.kirkukhealth.poster.controller;

import com.kirkukhealth.poster.model.Poster;
import com.kirkukhealth.poster.model.Notification;
import com.kirkukhealth.poster.service.PosterGovernanceService;
import com.kirkukhealth.poster.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Admin Governance Controller
 * متحكم حوكمة المدير
 * 
 * Handles poster approval workflow for Admin (Sector Manager)
 * يتعامل مع سير عمل الموافقة على البوسترات للمدير (مدير القطاع)
 */
@RestController
@RequestMapping("/api/admin/governance")
@CrossOrigin(origins = "*")
public class AdminGovernanceController {

    @Autowired
    private PosterGovernanceService governanceService;

    @Autowired
    private NotificationRepository notificationRepository;

    /**
     * Get pending posters (waiting for approval)
     * الحصول على البوسترات في الانتظار (تنتظر الموافقة)
     * 
     * GET /api/admin/governance/pending
     */
    @GetMapping("/pending")
    public ResponseEntity<Map<String, Object>> getPendingPosters() {
        try {
            List<Poster> pendingPosters = governanceService.getPendingPosters();
            
            Map<String, Object> response = new HashMap<>();
            response.put("count", pendingPosters.size());
            response.put("posters", pendingPosters);
            response.put("message", "البوسترات في انتظار الموافقة");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "خطأ في جلب البوسترات");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Approve a poster (Only Admin)
     * الموافقة على بوستر (فقط المدير)
     * 
     * POST /api/admin/governance/approve/{posterId}
     */
    @PostMapping("/approve/{posterId}")
    public ResponseEntity<Map<String, Object>> approvePoster(
            @PathVariable String posterId,
            @RequestHeader(value = "X-Admin-Id", required = false) String adminId) {
        
        try {
            if (adminId == null || adminId.isEmpty()) {
                adminId = "admin"; // Default admin ID (should come from authentication)
            }

            Poster approvedPoster = governanceService.approvePoster(posterId, adminId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("posterId", approvedPoster.getId());
            response.put("status", approvedPoster.getStatus().name());
            response.put("message", "تمت الموافقة على البوستر بنجاح - جاهز للطباعة بجودة 300 DPI");
            response.put("approvedAt", approvedPoster.getApprovedAt());
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "خطأ في الموافقة");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "خطأ في الخادم");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Get notifications (new submissions)
     * الحصول على الإشعارات (الإرسالات الجديدة)
     * 
     * GET /api/admin/governance/notifications
     */
    @GetMapping("/notifications")
    public ResponseEntity<Map<String, Object>> getNotifications(
            @RequestParam(value = "unreadOnly", defaultValue = "true") boolean unreadOnly) {
        
        try {
            List<Notification> notifications;
            if (unreadOnly) {
                notifications = notificationRepository.findByReadOrderByCreatedAtDesc(false);
            } else {
                notifications = notificationRepository.findAllByOrderByCreatedAtDesc();
            }

            long unreadCount = notificationRepository.countByRead(false);
            
            Map<String, Object> response = new HashMap<>();
            response.put("unreadCount", unreadCount);
            response.put("notifications", notifications);
            response.put("message", "الإشعارات");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "خطأ في جلب الإشعارات");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Mark notification as read
     * تحديد الإشعار كمقروء
     * 
     * PUT /api/admin/governance/notifications/{notificationId}/read
     */
    @PutMapping("/notifications/{notificationId}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable String notificationId) {
        try {
            Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

            notification.setRead(true);
            notificationRepository.save(notification);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "تم تحديد الإشعار كمقروء");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "خطأ في تحديث الإشعار");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Download approved poster (300 DPI)
     * تحميل البوستر المعتمد (300 DPI)
     * 
     * GET /api/admin/governance/posters/{posterId}/download
     */
    @GetMapping("/posters/{posterId}/download")
    public ResponseEntity<byte[]> downloadApprovedPoster(@PathVariable String posterId) {
        try {
            byte[] imageBytes = governanceService.downloadApprovedPoster(posterId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(imageBytes.length);
            headers.setContentDispositionFormData("attachment", "approved-poster-" + posterId + ".png");

            return ResponseEntity.ok()
                .headers(headers)
                .body(imageBytes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage().getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(("Error: " + e.getMessage()).getBytes());
        }
    }
}

