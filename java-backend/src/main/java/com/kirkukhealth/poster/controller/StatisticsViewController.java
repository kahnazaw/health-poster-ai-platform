package com.kirkukhealth.poster.controller;

import com.kirkukhealth.poster.model.UserProfile;
import com.kirkukhealth.poster.service.HealthStatisticsService;
import com.kirkukhealth.poster.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

/**
 * Statistics View Controller
 * متحكم عرض الإحصائيات
 * 
 * Serves the statistics entry form page
 * يخدم صفحة نموذج إدخال الإحصائيات
 */
@Controller
public class StatisticsViewController {

    @Autowired
    private HealthStatisticsService statisticsService;

    @Autowired
    private UserProfileService userProfileService;

    /**
     * Display statistics entry form
     * عرض نموذج إدخال الإحصائيات
     * 
     * GET /statistics/entry
     */
    @GetMapping("/statistics/entry")
    public String showStatisticsForm(Model model) {
        // Get all health centers
        List<UserProfile> centers = userProfileService.getAllHealthCentersWithStats();
        model.addAttribute("centers", centers);

        // Get all categories and topics
        Map<String, List<String>> categoryTopics = statisticsService.getAllCategoryTopics();
        model.addAttribute("categoryTopics", categoryTopics);
        model.addAttribute("categories", statisticsService.getAllCategories());

        return "statistics-entry";
    }

    /**
     * Display strategic dashboard
     * عرض لوحة التحكم الاستراتيجية
     * 
     * GET /statistics/dashboard
     */
    @GetMapping("/statistics/dashboard")
    public String showDashboard(Model model) {
        // Get all categories for filter
        model.addAttribute("categories", statisticsService.getAllCategories());
        
        // Get default summary (will be loaded via AJAX)
        model.addAttribute("defaultCategory", "صحة الأم والطفل");
        
        return "statistics-dashboard";
    }

    /**
     * Display target management page
     * عرض صفحة إدارة الأهداف
     * 
     * GET /statistics/targets
     */
    @GetMapping("/statistics/targets")
    public String showTargetManagement(Model model) {
        // Get all health centers
        List<UserProfile> centers = userProfileService.getAllHealthCentersWithStats();
        model.addAttribute("centers", centers);

        // Get all categories and topics
        Map<String, List<String>> categoryTopics = statisticsService.getAllCategoryTopics();
        model.addAttribute("categoryTopics", categoryTopics);
        model.addAttribute("categories", statisticsService.getAllCategories());

        // Get current month/year
        java.time.YearMonth currentMonth = java.time.YearMonth.now();
        model.addAttribute("currentYear", currentMonth.getYear());
        model.addAttribute("currentMonth", currentMonth.getMonthValue());

        return "target-management";
    }

    /**
     * Display communication center
     * عرض مركز الاتصالات الموحد
     * 
     * GET /communication
     */
    @GetMapping("/communication")
    public String showCommunicationCenter(Model model) {
        // Get all health centers for chat
        List<UserProfile> centers = userProfileService.getAllHealthCentersWithStats();
        model.addAttribute("centers", centers);

        // Get FAQ categories
        List<String> faqCategories = statisticsService.getAllCategories(); // Reuse for now
        model.addAttribute("faqCategories", faqCategories);

        return "communication-center";
    }

    /**
     * Display daily briefing dashboard (Admin landing page)
     * عرض لوحة الإحاطة اليومية (صفحة المدير الرئيسية)
     * 
     * GET /admin/dashboard
     */
    @GetMapping("/admin/dashboard")
    public String showAdminDashboard(Model model) {
        // Data will be loaded via AJAX
        return "admin-dashboard";
    }

    /**
     * Display AI-powered poster generator
     * عرض مولد البوسترات بالذكاء الاصطناعي
     * 
     * GET /posters/generator
     */
    @GetMapping("/posters/generator")
    public String showPosterGenerator(Model model) {
        // Get all categories and topics for dropdown
        Map<String, List<String>> categoryTopics = statisticsService.getAllCategoryTopics();
        model.addAttribute("categoryTopics", categoryTopics);
        return "poster-generator";
    }
}

