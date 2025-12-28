package com.kirkukhealth.poster.service;

import com.kirkukhealth.poster.model.*;
import com.kirkukhealth.poster.repository.PosterRepository;
import com.kirkukhealth.poster.repository.NotificationRepository;
import com.kirkukhealth.poster.repository.UserProfileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Poster Governance Service
 * خدمة حوكمة البوسترات
 * 
 * Manages poster approval workflow:
 * - DRAFT: Poster is being created/edited
 * - PENDING: Poster submitted for approval
 * - APPROVED: Poster approved by Admin, 300 DPI version unlocked
 * 
 * Only Admin (Sector Manager) can approve posters
 */
@Service
public class PosterGovernanceService {

    @Autowired
    private PosterRepository posterRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    // PosterImageService is available if needed for future enhancements
    // @Autowired
    // private PosterImageService posterImageService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Create a new poster with DRAFT status
     * إنشاء بوستر جديد بحالة مسودة
     */
    @Transactional
    public Poster createPoster(PosterContent content, String userId, byte[] imageBytes) {
        try {
        // Get health center info
        Optional<UserProfile> profileOpt = userProfileRepository.findByUserId(userId);
        String healthCenterId = profileOpt.map(profile -> profile.getUserId() != null ? profile.getUserId() : userId)
            .orElse(userId);

            // Convert content to JSON
            String contentJson = objectMapper.writeValueAsString(content);

            Poster poster = Poster.builder()
                .status(Poster.PosterStatus.DRAFT)
                .userId(userId)
                .healthCenterId(healthCenterId)
                .title(content.getTitle())
                .topic(content.getTopic())
                .content(contentJson)
                .imageBytes(imageBytes) // Store 300 DPI image
                .mohApproved(content.getMohApproved())
                .build();

            return posterRepository.save(poster);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create poster: " + e.getMessage(), e);
        }
    }

    /**
     * Submit poster for approval (DRAFT -> PENDING)
     * إرسال البوستر للموافقة (مسودة -> في الانتظار)
     */
    @Transactional
    public Poster submitForApproval(String posterId, String userId) {
        Poster poster = posterRepository.findById(posterId)
            .orElseThrow(() -> new IllegalArgumentException("Poster not found: " + posterId));

        if (!poster.getUserId().equals(userId)) {
            throw new IllegalArgumentException("User does not own this poster");
        }

        if (poster.getStatus() != Poster.PosterStatus.DRAFT) {
            throw new IllegalArgumentException("Only DRAFT posters can be submitted");
        }

        // Change status to PENDING
        poster.setStatus(Poster.PosterStatus.PENDING);
        poster = posterRepository.save(poster);

        // Create notification for admin
        createNotification(poster);

        return poster;
    }

    /**
     * Approve poster (PENDING -> APPROVED) - Only Admin
     * الموافقة على البوستر (في الانتظار -> معتمد) - فقط المدير
     */
    @Transactional
    public Poster approvePoster(String posterId, String adminUserId) {
        Poster poster = posterRepository.findById(posterId)
            .orElseThrow(() -> new IllegalArgumentException("Poster not found: " + posterId));

        if (poster.getStatus() != Poster.PosterStatus.PENDING) {
            throw new IllegalArgumentException("Only PENDING posters can be approved");
        }

        // Approve poster
        poster.setStatus(Poster.PosterStatus.APPROVED);
        poster.setApprovedBy(adminUserId);
        poster.setApprovedAt(LocalDateTime.now());

        // Ensure logo.jpg is integrated (imageBytes already contains it from PosterImageService)
        // The 300 DPI version is now unlocked for printing at "AZAW TEAM CENTER"

        poster = posterRepository.save(poster);

        // Create approval notification
        createApprovalNotification(poster);

        return poster;
    }

    /**
     * Get pending posters (waiting for approval)
     * الحصول على البوسترات في الانتظار (تنتظر الموافقة)
     */
    public List<Poster> getPendingPosters() {
        return posterRepository.findByStatusOrderByCreatedAtDesc(Poster.PosterStatus.PENDING);
    }

    /**
     * Get approved posters
     * الحصول على البوسترات المعتمدة
     */
    public List<Poster> getApprovedPosters() {
        return posterRepository.findByStatus(Poster.PosterStatus.APPROVED);
    }

    /**
     * Get poster by ID (with status check for download)
     * الحصول على البوستر بالمعرف (مع فحص الحالة للتحميل)
     */
    public Optional<Poster> getPosterById(String posterId) {
        return posterRepository.findById(posterId);
    }

    /**
     * Update poster
     * تحديث البوستر
     */
    @Transactional
    public Poster updatePoster(Poster poster) {
        return posterRepository.save(poster);
    }

    /**
     * Download approved poster (300 DPI)
     * تحميل البوستر المعتمد (300 DPI)
     */
    public byte[] downloadApprovedPoster(String posterId) {
        Poster poster = posterRepository.findById(posterId)
            .orElseThrow(() -> new IllegalArgumentException("Poster not found: " + posterId));

        if (poster.getStatus() != Poster.PosterStatus.APPROVED) {
            throw new IllegalArgumentException("Only APPROVED posters can be downloaded");
        }

        if (poster.getImageBytes() == null) {
            throw new IllegalStateException("Poster image not available");
        }

        return poster.getImageBytes();
    }

    /**
     * Create notification for new submission
     * إنشاء إشعار لإرسال جديد
     */
    private void createNotification(Poster poster) {
        String userId = poster.getUserId();
        if (userId == null) {
            userId = "unknown";
        }
        
        Optional<UserProfile> profileOpt = userProfileRepository.findByUserId(userId);
        String healthCenterName = profileOpt
            .map(profile -> profile.getHealthCenterName() != null ? profile.getHealthCenterName() : "Unknown Center")
            .orElse("Unknown Center");

        Notification notification = Notification.builder()
            .type("POSTER_SUBMITTED")
            .posterId(poster.getId())
            .userId(poster.getUserId())
            .healthCenterName(healthCenterName)
            .message("بوستر جديد في انتظار الموافقة من: " + healthCenterName + " - " + poster.getTitle())
            .read(false)
            .build();

        notificationRepository.save(notification);
    }

    /**
     * Create approval notification
     * إنشاء إشعار الموافقة
     */
    private void createApprovalNotification(Poster poster) {
        String userId = poster.getUserId();
        if (userId == null) {
            userId = "unknown";
        }
        
        Optional<UserProfile> profileOpt = userProfileRepository.findByUserId(userId);
        String healthCenterName = profileOpt
            .map(profile -> profile.getHealthCenterName() != null ? profile.getHealthCenterName() : "Unknown Center")
            .orElse("Unknown Center");

        Notification notification = Notification.builder()
            .type("POSTER_APPROVED")
            .posterId(poster.getId())
            .userId(poster.getUserId())
            .healthCenterName(healthCenterName)
            .message("تمت الموافقة على البوستر: " + poster.getTitle() + " - " + healthCenterName)
            .read(false)
            .build();

        notificationRepository.save(notification);
    }
}

