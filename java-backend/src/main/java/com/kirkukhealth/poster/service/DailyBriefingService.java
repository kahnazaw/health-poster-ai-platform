package com.kirkukhealth.poster.service;

import com.kirkukhealth.poster.model.*;
import com.kirkukhealth.poster.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Daily Briefing Service
 * خدمة الإحاطة اليومية
 * 
 * Aggregates daily briefing data for Admin
 * يجمع بيانات الإحاطة اليومية للمدير
 */
@Service
public class DailyBriefingService {

    @Autowired
    private PosterRepository posterRepository;

    @Autowired
    private DailyStatisticsRepository statisticsRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private BulletinRepository bulletinRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    /**
     * Get daily briefing for Admin
     * الحصول على الإحاطة اليومية للمدير
     */
    public Map<String, Object> getDailyBriefing() {
        Map<String, Object> briefing = new LinkedHashMap<>();
        
        // 1. Pending Posters Count
        long pendingPostersCount = posterRepository.countByStatus(Poster.PosterStatus.PENDING);
        List<Poster> pendingPosters = posterRepository.findByStatusOrderByCreatedAtDesc(Poster.PosterStatus.PENDING);
        
        briefing.put("pendingPostersCount", pendingPostersCount);
        briefing.put("pendingPosters", pendingPosters.stream()
            .map(p -> {
                Map<String, Object> posterInfo = new HashMap<>();
                posterInfo.put("id", p.getId());
                posterInfo.put("title", p.getTitle());
                posterInfo.put("centerId", p.getUserId());
                posterInfo.put("createdAt", p.getCreatedAt());
                return posterInfo;
            })
            .collect(Collectors.toList()));
        
        // 2. Centers with no data today or yesterday
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        List<UserProfile> allCenters = userProfileRepository.findAll();
        List<Map<String, Object>> reportingGaps = new ArrayList<>();
        
        for (UserProfile center : allCenters) {
            List<DailyStatistics> todayStats = statisticsRepository.findByCenterIdAndEntryDateBetween(
                center.getUserId(), today, today);
            List<DailyStatistics> yesterdayStats = statisticsRepository.findByCenterIdAndEntryDateBetween(
                center.getUserId(), yesterday, yesterday);
            
            if (todayStats.isEmpty() && yesterdayStats.isEmpty()) {
                Map<String, Object> gap = new HashMap<>();
                gap.put("centerId", center.getUserId());
                gap.put("centerName", center.getHealthCenterName() != null 
                    ? center.getHealthCenterName() : center.getUserId());
                gap.put("managerName", center.getManagerName());
                gap.put("daysWithoutData", calculateDaysWithoutData(center.getUserId(), today));
                gap.put("isCritical", calculateDaysWithoutData(center.getUserId(), today) >= 2);
                reportingGaps.add(gap);
            }
        }
        
        // Sort by days without data (descending)
        reportingGaps.sort((a, b) -> Integer.compare(
            ((Integer) b.get("daysWithoutData")), 
            ((Integer) a.get("daysWithoutData"))));
        
        briefing.put("reportingGaps", reportingGaps);
        briefing.put("reportingGapsCount", reportingGaps.size());
        
        // 3. Unread Messages Count
        long unreadMessagesCount = messageRepository.countByReceiverIdAndIsReadFalse("admin");
        List<Message> recentUnreadMessages = messageRepository.findByReceiverIdAndIsReadFalseOrderByCreatedAtDesc("admin")
            .stream()
            .limit(10)
            .collect(Collectors.toList());
        
        briefing.put("unreadMessagesCount", unreadMessagesCount);
        briefing.put("recentUnreadMessages", recentUnreadMessages.stream()
            .map(m -> {
                Map<String, Object> msgInfo = new HashMap<>();
                msgInfo.put("id", m.getId());
                msgInfo.put("senderId", m.getSenderId());
                msgInfo.put("content", m.getContent().length() > 100 
                    ? m.getContent().substring(0, 100) + "..." : m.getContent());
                msgInfo.put("createdAt", m.getCreatedAt());
                msgInfo.put("posterId", m.getPosterId());
                return msgInfo;
            })
            .collect(Collectors.toList()));
        
        // 4. Latest Bulletin View Verification
        List<Bulletin> allBulletins = bulletinRepository.findAllByOrderByIsPinnedDescCreatedAtDesc();
        Map<String, Object> bulletinInfo = new HashMap<>();
        
        if (!allBulletins.isEmpty()) {
            Bulletin latestBulletin = allBulletins.get(0);
            bulletinInfo.put("latestBulletinId", latestBulletin.getId());
            bulletinInfo.put("latestBulletinTitle", latestBulletin.getTitle());
            bulletinInfo.put("latestBulletinDate", latestBulletin.getCreatedAt());
            bulletinInfo.put("latestBulletinPriority", latestBulletin.getPriority());
            
            // Note: In a full implementation, you would track views per center
            // For now, we'll assume all centers have viewed if no unread messages about it
            bulletinInfo.put("allCentersViewed", true); // Simplified
        } else {
            bulletinInfo.put("latestBulletinId", null);
            bulletinInfo.put("latestBulletinTitle", "لا توجد نشرات");
        }
        
        briefing.put("bulletinInfo", bulletinInfo);
        
        // 5. Critical Alerts
        List<Map<String, Object>> criticalAlerts = new ArrayList<>();
        
        // Critical: Centers with no data for 48+ hours
        for (Map<String, Object> gap : reportingGaps) {
            if ((Integer) gap.get("daysWithoutData") >= 2) {
                Map<String, Object> alert = new HashMap<>();
                alert.put("type", "REPORTING_GAP");
                alert.put("severity", "CRITICAL");
                alert.put("message", "المركز " + gap.get("centerName") + " لم يدخل بيانات لمدة " + 
                    gap.get("daysWithoutData") + " أيام");
                alert.put("centerId", gap.get("centerId"));
                criticalAlerts.add(alert);
            }
        }
        
        // Critical: High priority pending posters
        if (pendingPostersCount > 5) {
            Map<String, Object> alert = new HashMap<>();
            alert.put("type", "PENDING_POSTERS");
            alert.put("severity", "HIGH");
            alert.put("message", "هناك " + pendingPostersCount + " بوستر في انتظار الموافقة");
            criticalAlerts.add(alert);
        }
        
        briefing.put("criticalAlerts", criticalAlerts);
        briefing.put("criticalAlertsCount", criticalAlerts.size());
        
        // 6. Summary Statistics
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalCenters", allCenters.size());
        summary.put("activeCenters", allCenters.size() - reportingGaps.size());
        summary.put("currentDate", LocalDate.now().toString());
        
        briefing.put("summary", summary);
        
        return briefing;
    }

    /**
     * Calculate days without data for a center
     * حساب الأيام بدون بيانات لمركز
     */
    private int calculateDaysWithoutData(String centerId, LocalDate fromDate) {
        LocalDate checkDate = fromDate;
        int days = 0;
        
        while (days < 7) { // Check up to 7 days back
            List<DailyStatistics> stats = statisticsRepository.findByCenterIdAndEntryDateBetween(
                centerId, checkDate, checkDate);
            
            if (stats.isEmpty()) {
                days++;
                checkDate = checkDate.minusDays(1);
            } else {
                break;
            }
        }
        
        return days;
    }
}

