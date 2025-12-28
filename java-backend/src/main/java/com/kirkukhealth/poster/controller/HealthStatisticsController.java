package com.kirkukhealth.poster.controller;

import com.kirkukhealth.poster.model.DailyStatistics;
import com.kirkukhealth.poster.model.MonthlyTarget;
import com.kirkukhealth.poster.service.HealthStatisticsService;
import com.kirkukhealth.poster.service.StatisticsExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Health Statistics Controller
 * متحكم إحصائيات الصحة
 * 
 * Handles daily statistics entry and monthly reporting
 * يتعامل مع إدخال الإحصائيات اليومية والتقارير الشهرية
 */
@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "*")
public class HealthStatisticsController {

    @Autowired
    private HealthStatisticsService statisticsService;

    @Autowired
    private StatisticsExportService exportService;

    /**
     * Get all categories
     * الحصول على جميع الفئات
     * 
     * GET /api/statistics/categories
     */
    @GetMapping("/categories")
    public ResponseEntity<Map<String, Object>> getCategories() {
        Map<String, Object> response = new HashMap<>();
        response.put("categories", statisticsService.getAllCategories());
        response.put("categoryTopics", statisticsService.getAllCategoryTopics());
        response.put("message", "جميع الفئات والمواضيع");
        return ResponseEntity.ok(response);
    }

    /**
     * Get all categories and topics (for poster generator)
     * الحصول على جميع الفئات والمواضيع (لمولد البوسترات)
     * 
     * GET /api/statistics/categories-topics
     */
    @GetMapping("/categories-topics")
    public ResponseEntity<Map<String, List<String>>> getCategoriesTopics() {
        return ResponseEntity.ok(statisticsService.getAllCategoryTopics());
    }

    /**
     * Get topics for a category
     * الحصول على المواضيع لفئة معينة
     * 
     * GET /api/statistics/categories/{categoryName}/topics
     */
    @GetMapping("/categories/{categoryName}/topics")
    public ResponseEntity<Map<String, Object>> getTopics(@PathVariable String categoryName) {
        List<String> topics = statisticsService.getTopicsForCategory(categoryName);
        
        Map<String, Object> response = new HashMap<>();
        response.put("categoryName", categoryName);
        response.put("topics", topics);
        response.put("message", "مواضيع الفئة: " + categoryName);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Create or update daily statistics entry
     * إنشاء أو تحديث إدخال إحصائيات يومية
     * 
     * POST /api/statistics/daily
     */
    @PostMapping("/daily")
    public ResponseEntity<Map<String, Object>> saveDailyStatistics(
            @RequestBody DailyStatisticsRequest request) {
        
        try {
            DailyStatistics statistics = DailyStatistics.builder()
                .centerId(request.getCenterId())
                .categoryName(request.getCategoryName())
                .topicName(request.getTopicName())
                .individualMeetings(request.getIndividualMeetings() != null ? request.getIndividualMeetings() : 0)
                .lectures(request.getLectures() != null ? request.getLectures() : 0)
                .seminars(request.getSeminars() != null ? request.getSeminars() : 0)
                .entryDate(request.getEntryDate() != null ? request.getEntryDate() : LocalDate.now())
                .build();

            DailyStatistics saved = statisticsService.saveDailyStatistics(statistics);

            Map<String, Object> response = new HashMap<>();
            response.put("id", saved.getId());
            response.put("message", "تم حفظ الإحصائيات بنجاح");
            response.put("statistics", saved);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "خطأ في البيانات");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "خطأ في الخادم");
            error.put("message", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Get daily statistics for a center
     * الحصول على الإحصائيات اليومية لمركز
     * 
     * GET /api/statistics/daily?centerId={centerId}&startDate={startDate}&endDate={endDate}
     */
    @GetMapping("/daily")
    public ResponseEntity<Map<String, Object>> getDailyStatistics(
            @RequestParam String centerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        List<DailyStatistics> statistics = statisticsService.getDailyStatistics(centerId, startDate, endDate);

        Map<String, Object> response = new HashMap<>();
        response.put("centerId", centerId);
        response.put("startDate", startDate);
        response.put("endDate", endDate);
        response.put("count", statistics.size());
        response.put("statistics", statistics);

        return ResponseEntity.ok(response);
    }

    /**
     * Get monthly totals for a center
     * الحصول على الإجماليات الشهرية لمركز
     * 
     * GET /api/statistics/monthly?centerId={centerId}&year={year}&month={month}
     */
    @GetMapping("/monthly")
    public ResponseEntity<Map<String, Object>> getMonthlyTotals(
            @RequestParam String centerId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        
        if (year == null) year = LocalDate.now().getYear();
        if (month == null) month = LocalDate.now().getMonthValue();

        YearMonth yearMonth = YearMonth.of(year, month);
        Map<String, Object> totals = statisticsService.getMonthlyTotals(centerId, yearMonth);

        return ResponseEntity.ok(totals);
    }

    /**
     * Get all centers' monthly totals (for admin report)
     * الحصول على إجماليات جميع المراكز الشهرية (لتقرير المدير)
     * 
     * GET /api/statistics/monthly/all?year={year}&month={month}
     */
    @GetMapping("/monthly/all")
    public ResponseEntity<Map<String, Object>> getAllCentersMonthlyTotals(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        
        if (year == null) year = LocalDate.now().getYear();
        if (month == null) month = LocalDate.now().getMonthValue();

        YearMonth yearMonth = YearMonth.of(year, month);
        Map<String, Object> totals = statisticsService.getAllCentersMonthlyTotals(yearMonth);

        return ResponseEntity.ok(totals);
    }

    /**
     * Get strategic dashboard summary
     * الحصول على ملخص لوحة التحكم الاستراتيجية
     * 
     * GET /api/statistics/summary?category={categoryName}
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getDashboardSummary(
            @RequestParam(required = false) String category) {
        
        Map<String, Object> summary = statisticsService.getDashboardSummary(category);
        
        // Add progress data
        YearMonth currentMonth = YearMonth.now();
        Map<String, Object> progressData = statisticsService.getAllCentersProgress(
            currentMonth.getYear(), currentMonth.getMonthValue());
        summary.put("progressData", progressData);
        
        return ResponseEntity.ok(summary);
    }

    /**
     * Daily Statistics Request DTO
     */
    public static class DailyStatisticsRequest {
        private String centerId;
        private String categoryName;
        private String topicName;
        private Integer individualMeetings;
        private Integer lectures;
        private Integer seminars;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate entryDate;

        // Getters and Setters
        public String getCenterId() { return centerId; }
        public void setCenterId(String centerId) { this.centerId = centerId; }
        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
        public String getTopicName() { return topicName; }
        public void setTopicName(String topicName) { this.topicName = topicName; }
        public Integer getIndividualMeetings() { return individualMeetings; }
        public void setIndividualMeetings(Integer individualMeetings) { this.individualMeetings = individualMeetings; }
        public Integer getLectures() { return lectures; }
        public void setLectures(Integer lectures) { this.lectures = lectures; }
        public Integer getSeminars() { return seminars; }
        public void setSeminars(Integer seminars) { this.seminars = seminars; }
        public LocalDate getEntryDate() { return entryDate; }
        public void setEntryDate(LocalDate entryDate) { this.entryDate = entryDate; }
    }

    /**
     * Export monthly report to Excel
     * تصدير التقرير الشهري إلى Excel
     * 
     * GET /api/statistics/export/monthly?year={year}&month={month}&centerId={centerId}
     */
    @GetMapping("/export/monthly")
    public ResponseEntity<byte[]> exportMonthlyReport(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String centerId) {
        
        try {
            if (year == null) year = LocalDate.now().getYear();
            if (month == null) month = LocalDate.now().getMonthValue();

            YearMonth yearMonth = YearMonth.of(year, month);
            byte[] excelBytes = exportService.exportMonthlyReportToExcel(yearMonth, centerId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", 
                "health-statistics-report-" + yearMonth.toString() + 
                (centerId != null ? "-" + centerId : "") + ".xlsx");
            headers.setContentLength(excelBytes.length);

            return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(("Error exporting report: " + e.getMessage()).getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(("Unexpected error: " + e.getMessage()).getBytes());
        }
    }

    /**
     * Get progress for a center
     * الحصول على التقدم لمركز
     * 
     * GET /api/statistics/progress?centerId={centerId}&year={year}&month={month}
     */
    @GetMapping("/progress")
    public ResponseEntity<Map<String, Object>> getProgress(
            @RequestParam String centerId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        
        if (year == null) year = LocalDate.now().getYear();
        if (month == null) month = LocalDate.now().getMonthValue();

        Map<String, Object> progress = statisticsService.calculateProgress(centerId, year, month);
        return ResponseEntity.ok(progress);
    }

    /**
     * Get all centers' progress
     * الحصول على تقدم جميع المراكز
     * 
     * GET /api/statistics/progress/all?year={year}&month={month}
     */
    @GetMapping("/progress/all")
    public ResponseEntity<Map<String, Object>> getAllCentersProgress(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        
        if (year == null) year = LocalDate.now().getYear();
        if (month == null) month = LocalDate.now().getMonthValue();

        Map<String, Object> progress = statisticsService.getAllCentersProgress(year, month);
        return ResponseEntity.ok(progress);
    }

    /**
     * Save or update monthly target
     * حفظ أو تحديث الهدف الشهري
     * 
     * POST /api/statistics/targets
     */
    @PostMapping("/targets")
    public ResponseEntity<Map<String, Object>> saveTarget(
            @RequestBody MonthlyTargetRequest request) {
        
        try {
            MonthlyTarget target = MonthlyTarget.builder()
                .centerId(request.getCenterId())
                .categoryName(request.getCategoryName())
                .topicName(request.getTopicName())
                .targetMeetings(request.getTargetMeetings() != null ? request.getTargetMeetings() : 0)
                .targetLectures(request.getTargetLectures() != null ? request.getTargetLectures() : 0)
                .targetSeminars(request.getTargetSeminars() != null ? request.getTargetSeminars() : 0)
                .targetYear(request.getTargetYear())
                .targetMonth(request.getTargetMonth())
                .build();

            MonthlyTarget saved = statisticsService.saveTarget(target);

            Map<String, Object> response = new HashMap<>();
            response.put("id", saved.getId());
            response.put("message", "تم حفظ الهدف بنجاح");
            response.put("target", saved);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "خطأ في البيانات");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "خطأ في الخادم");
            error.put("message", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Get targets for a center
     * الحصول على الأهداف لمركز
     * 
     * GET /api/statistics/targets?centerId={centerId}&year={year}&month={month}
     */
    @GetMapping("/targets")
    public ResponseEntity<Map<String, Object>> getTargets(
            @RequestParam String centerId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        
        if (year == null) year = LocalDate.now().getYear();
        if (month == null) month = LocalDate.now().getMonthValue();

        List<MonthlyTarget> targets = statisticsService.getTargets(centerId, year, month);

        Map<String, Object> response = new HashMap<>();
        response.put("centerId", centerId);
        response.put("year", year);
        response.put("month", month);
        response.put("targets", targets);
        response.put("count", targets.size());

        return ResponseEntity.ok(response);
    }

    /**
     * Monthly Target Request DTO
     */
    public static class MonthlyTargetRequest {
        private String centerId;
        private String categoryName;
        private String topicName;
        private Integer targetMeetings;
        private Integer targetLectures;
        private Integer targetSeminars;
        private Integer targetYear;
        private Integer targetMonth;

        // Getters and Setters
        public String getCenterId() { return centerId; }
        public void setCenterId(String centerId) { this.centerId = centerId; }
        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
        public String getTopicName() { return topicName; }
        public void setTopicName(String topicName) { this.topicName = topicName; }
        public Integer getTargetMeetings() { return targetMeetings; }
        public void setTargetMeetings(Integer targetMeetings) { this.targetMeetings = targetMeetings; }
        public Integer getTargetLectures() { return targetLectures; }
        public void setTargetLectures(Integer targetLectures) { this.targetLectures = targetLectures; }
        public Integer getTargetSeminars() { return targetSeminars; }
        public void setTargetSeminars(Integer targetSeminars) { this.targetSeminars = targetSeminars; }
        public Integer getTargetYear() { return targetYear; }
        public void setTargetYear(Integer targetYear) { this.targetYear = targetYear; }
        public Integer getTargetMonth() { return targetMonth; }
        public void setTargetMonth(Integer targetMonth) { this.targetMonth = targetMonth; }
    }
}

