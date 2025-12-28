package com.kirkukhealth.poster.service;

import com.kirkukhealth.poster.model.HealthOccasion;
import com.kirkukhealth.poster.repository.HealthOccasionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

/**
 * Health Occasion Service
 * خدمة المناسبات الصحية
 * 
 * Manages global health awareness occasions and smart suggestions
 * يدير المناسبات الصحية العالمية والاقتراحات الذكية
 */
@Service
public class HealthOccasionService {

    @Autowired
    private HealthOccasionRepository occasionRepository;

    @Autowired
    private HealthStatisticsService healthStatisticsService;

    /**
     * Get active occasions for today
     * الحصول على المناسبات النشطة لليوم
     */
    public List<HealthOccasion> getTodayOccasions() {
        return occasionRepository.findActiveOccasionsForDate(LocalDate.now());
    }

    /**
     * Get smart suggestions for poster generation based on current date
     * الحصول على اقتراحات ذكية لتوليد البوسترات بناءً على التاريخ الحالي
     * 
     * @return Map with occasion info and suggested topics
     */
    public Map<String, Object> getSmartSuggestions() {
        Map<String, Object> suggestions = new HashMap<>();
        List<HealthOccasion> todayOccasions = getTodayOccasions();

        if (todayOccasions.isEmpty()) {
            suggestions.put("hasSuggestion", false);
            suggestions.put("message", "لا توجد مناسبة صحية خاصة اليوم");
            return suggestions;
        }

        // Get the highest priority occasion
        HealthOccasion topOccasion = todayOccasions.stream()
            .max(Comparator.comparing(HealthOccasion::getPriority))
            .orElse(todayOccasions.get(0));

        suggestions.put("hasSuggestion", true);
        suggestions.put("occasion", Map.of(
            "name", topOccasion.getOccasionNameAr(),
            "nameEn", topOccasion.getOccasionNameEn() != null ? topOccasion.getOccasionNameEn() : "",
            "description", topOccasion.getDescriptionAr() != null ? topOccasion.getDescriptionAr() : "",
            "category", topOccasion.getCategory() != null ? topOccasion.getCategory() : "",
            "priority", topOccasion.getPriority()
        ));

        // Parse suggested topics
        List<String> suggestedTopics = new ArrayList<>();
        if (topOccasion.getSuggestedTopics() != null && !topOccasion.getSuggestedTopics().isEmpty()) {
            String[] topics = topOccasion.getSuggestedTopics().split(",");
            for (String topic : topics) {
                String trimmed = topic.trim();
                if (!trimmed.isEmpty()) {
                    suggestedTopics.add(trimmed);
                }
            }
        }

        // If no suggested topics, get topics from the category
        if (suggestedTopics.isEmpty() && topOccasion.getCategory() != null) {
            Map<String, List<String>> allTopics = healthStatisticsService.getAllCategoryTopics();
            List<String> categoryTopics = allTopics.get(topOccasion.getCategory());
            if (categoryTopics != null) {
                suggestedTopics.addAll(categoryTopics);
            }
        }

        suggestions.put("suggestedTopics", suggestedTopics);
        suggestions.put("message", String.format(
            "اليوم هو %s. نقترح إنشاء بوستر توعوي لهذه المناسبة!",
            topOccasion.getOccasionNameAr()
        ));

        return suggestions;
    }

    /**
     * Get occasions in a date range
     * الحصول على المناسبات في نطاق تاريخي
     */
    public List<HealthOccasion> getOccasionsInRange(LocalDate startDate, LocalDate endDate) {
        return occasionRepository.findOccasionsInRange(startDate, endDate);
    }

    /**
     * Get all active occasions
     * الحصول على جميع المناسبات النشطة
     */
    public List<HealthOccasion> getAllActiveOccasions() {
        return occasionRepository.findByIsActiveTrueOrderByOccasionDateAsc();
    }

    /**
     * Get occasions for a specific month
     * الحصول على المناسبات لشهر محدد
     */
    public List<HealthOccasion> getOccasionsForMonth(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        return getOccasionsInRange(startDate, endDate);
    }

    /**
     * Get recommended tone for an occasion
     * الحصول على النبرة الموصى بها لمناسبة
     */
    public String getRecommendedTone(HealthOccasion occasion) {
        if (occasion.getCategory() == null) {
            return "formal";
        }

        String category = occasion.getCategory();
        if (category.contains("معدية") || category.contains("طوارئ")) {
            return "emergency";
        } else if (category.contains("تعليم") || category.contains("تغذية") || category.contains("نظافة")) {
            return "educational";
        } else {
            return "formal";
        }
    }
}


