package com.kirkukhealth.poster.service;

import com.kirkukhealth.poster.model.DailyStatistics;
import com.kirkukhealth.poster.model.MonthlyTarget;
import com.kirkukhealth.poster.model.UserProfile;
import com.kirkukhealth.poster.repository.DailyStatisticsRepository;
import com.kirkukhealth.poster.repository.MonthlyTargetRepository;
import com.kirkukhealth.poster.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

/**
 * Health Statistics Service
 * خدمة إحصائيات الصحة
 * 
 * Manages daily statistics entry and monthly aggregation
 * يدير إدخال الإحصائيات اليومية والتجميع الشهري
 */
@Service
public class HealthStatisticsService {

    @Autowired
    private DailyStatisticsRepository statisticsRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private MonthlyTargetRepository targetRepository;

    /**
     * Category mapping - 11 main sections with sub-topics
     * خريطة الفئات - 11 قسم رئيسي مع المواضيع الفرعية
     */
    public static final Map<String, List<String>> CATEGORY_TOPICS = new LinkedHashMap<>();

    static {
        // 1. Maternal & Child Health | صحة الأم والطفل
        CATEGORY_TOPICS.put("صحة الأم والطفل", Arrays.asList(
            "رعاية ما قبل الولادة",
            "رعاية ما بعد الولادة",
            "الرضاعة الطبيعية",
            "تغذية الرضع والأطفال",
            "نمو وتطور الطفل",
            "صحة المراهقين"
        ));

        // 2. Immunization | التطعيم
        CATEGORY_TOPICS.put("التطعيم", Arrays.asList(
            "التطعيم الروتيني",
            "التطعيم الموسمي",
            "أهمية التطعيم",
            "جدول التطعيمات",
            "التطعيم للأطفال",
            "التطعيم للبالغين"
        ));

        // 3. Communicable Diseases | الأمراض المعدية
        CATEGORY_TOPICS.put("الأمراض المعدية", Arrays.asList(
            "الوقاية من الإنفلونزا",
            "الوقاية من COVID-19",
            "الوقاية من السل",
            "الوقاية من التهاب الكبد",
            "الوقاية من الملاريا",
            "النظافة الشخصية للوقاية",
            "التباعد الاجتماعي"
        ));

        // 4. Non-Communicable Diseases | الأمراض غير المعدية
        CATEGORY_TOPICS.put("الأمراض غير المعدية", Arrays.asList(
            "الوقاية من السكري",
            "الوقاية من أمراض القلب",
            "الوقاية من السرطان",
            "الوقاية من ارتفاع ضغط الدم",
            "الوقاية من السمنة",
            "النشاط البدني",
            "التغذية الصحية"
        ));

        // 5. Mental Health | الصحة النفسية
        CATEGORY_TOPICS.put("الصحة النفسية", Arrays.asList(
            "الصحة النفسية العامة",
            "التعامل مع التوتر",
            "الصحة النفسية للأطفال",
            "الصحة النفسية للمراهقين",
            "الصحة النفسية لكبار السن",
            "الوقاية من الاكتئاب"
        ));

        // 6. First Aid | الإسعافات الأولية
        CATEGORY_TOPICS.put("الإسعافات الأولية", Arrays.asList(
            "الإسعافات الأولية الأساسية",
            "الإسعافات الأولية للأطفال",
            "الإسعافات الأولية في حالات الطوارئ",
            "إنعاش القلب والرئتين (CPR)",
            "معالجة الجروح",
            "معالجة الحروق"
        ));

        // 7. Hygiene | النظافة
        CATEGORY_TOPICS.put("النظافة", Arrays.asList(
            "النظافة الشخصية",
            "نظافة الأسنان",
            "نظافة اليدين",
            "نظافة البيئة",
            "نظافة الغذاء",
            "نظافة المياه"
        ));

        // 8. Drug Misuse | سوء استخدام الأدوية
        CATEGORY_TOPICS.put("سوء استخدام الأدوية", Arrays.asList(
            "الاستخدام الصحيح للأدوية",
            "مخاطر إساءة استخدام الأدوية",
            "التداخل الدوائي",
            "الجرعات الصحيحة",
            "تخزين الأدوية",
            "التخلص من الأدوية المنتهية"
        ));

        // 9. Antimicrobial Resistance | مقاومة المضادات الحيوية
        CATEGORY_TOPICS.put("مقاومة المضادات الحيوية", Arrays.asList(
            "الاستخدام الصحيح للمضادات الحيوية",
            "مخاطر مقاومة المضادات الحيوية",
            "الوقاية من مقاومة المضادات الحيوية",
            "متى تستخدم المضادات الحيوية",
            "إكمال دورة المضادات الحيوية"
        ));

        // 10. Health Occasions | المناسبات الصحية
        CATEGORY_TOPICS.put("المناسبات الصحية", Arrays.asList(
            "اليوم العالمي للصحة",
            "اليوم العالمي لمكافحة التدخين",
            "اليوم العالمي للسكري",
            "اليوم العالمي للقلب",
            "أسبوع التطعيم",
            "أسبوع الرضاعة الطبيعية",
            "مناسبات صحية أخرى"
        ));

        // 11. Others | أخرى
        CATEGORY_TOPICS.put("أخرى", Arrays.asList(
            "مواضيع صحية عامة",
            "الوعي الصحي",
            "الخدمات الصحية المتاحة",
            "مواضيع أخرى"
        ));
    }

    /**
     * Get all categories
     * الحصول على جميع الفئات
     */
    public List<String> getAllCategories() {
        return new ArrayList<>(CATEGORY_TOPICS.keySet());
    }

    /**
     * Get topics for a category
     * الحصول على المواضيع لفئة معينة
     */
    public List<String> getTopicsForCategory(String categoryName) {
        return CATEGORY_TOPICS.getOrDefault(categoryName, Collections.emptyList());
    }

    /**
     * Get all category-topic mappings
     * الحصول على جميع خريطة الفئات والمواضيع
     */
    public Map<String, List<String>> getAllCategoryTopics() {
        return new LinkedHashMap<>(CATEGORY_TOPICS);
    }

    /**
     * Create or update daily statistics entry
     * إنشاء أو تحديث إدخال إحصائيات يومية
     */
    @Transactional
    public DailyStatistics saveDailyStatistics(DailyStatistics statistics) {
        // Validate center exists
        Optional<UserProfile> profileOpt = userProfileRepository.findByUserId(statistics.getCenterId());
        if (profileOpt.isEmpty()) {
            throw new IllegalArgumentException("Health center not found: " + statistics.getCenterId());
        }

        // Validate category and topic
        if (!CATEGORY_TOPICS.containsKey(statistics.getCategoryName())) {
            throw new IllegalArgumentException("Invalid category: " + statistics.getCategoryName());
        }

        List<String> validTopics = CATEGORY_TOPICS.get(statistics.getCategoryName());
        if (!validTopics.contains(statistics.getTopicName())) {
            throw new IllegalArgumentException("Invalid topic for category: " + statistics.getTopicName());
        }

        // Check if entry already exists for this date, center, category, and topic
        Optional<DailyStatistics> existing = statisticsRepository.findByCenterIdAndCategoryNameAndEntryDateBetween(
            statistics.getCenterId(),
            statistics.getCategoryName(),
            statistics.getEntryDate(),
            statistics.getEntryDate()
        ).stream()
        .filter(s -> s.getTopicName().equals(statistics.getTopicName()))
        .findFirst();

        if (existing.isPresent()) {
            // Update existing entry
            DailyStatistics existingStats = existing.get();
            existingStats.setIndividualMeetings(statistics.getIndividualMeetings());
            existingStats.setLectures(statistics.getLectures());
            existingStats.setSeminars(statistics.getSeminars());
            return statisticsRepository.save(existingStats);
        } else {
            // Create new entry
            return statisticsRepository.save(statistics);
        }
    }

    /**
     * Get daily statistics for a center
     * الحصول على الإحصائيات اليومية لمركز
     */
    public List<DailyStatistics> getDailyStatistics(String centerId, LocalDate startDate, LocalDate endDate) {
        return statisticsRepository.findByCenterIdAndEntryDateBetween(centerId, startDate, endDate);
    }

    /**
     * Aggregate monthly totals for a center
     * تجميع الإجماليات الشهرية لمركز
     */
    public Map<String, Object> getMonthlyTotals(String centerId, YearMonth yearMonth) {
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Object[]> results = statisticsRepository.aggregateMonthlyTotals(centerId, startDate, endDate);

        Map<String, Object> totals = new LinkedHashMap<>();
        totals.put("centerId", centerId);
        totals.put("yearMonth", yearMonth.toString());
        totals.put("startDate", startDate);
        totals.put("endDate", endDate);

        List<Map<String, Object>> categoryTotals = new ArrayList<>();
        Map<String, Map<String, Object>> categoryMap = new LinkedHashMap<>();

        for (Object[] row : results) {
            String categoryName = (String) row[0];
            String topicName = (String) row[1];
            Long totalMeetings = ((Number) row[2]).longValue();
            Long totalLectures = ((Number) row[3]).longValue();
            Long totalSeminars = ((Number) row[4]).longValue();

            categoryMap.putIfAbsent(categoryName, new LinkedHashMap<>());
            Map<String, Object> categoryData = categoryMap.get(categoryName);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> topics = (List<Map<String, Object>>) categoryData.getOrDefault("topics", new ArrayList<>());

            Map<String, Object> topicData = new LinkedHashMap<>();
            topicData.put("topicName", topicName);
            topicData.put("individualMeetings", totalMeetings);
            topicData.put("lectures", totalLectures);
            topicData.put("seminars", totalSeminars);
            topicData.put("total", totalMeetings + totalLectures + totalSeminars);

            topics.add(topicData);
            categoryData.put("topics", topics);

            // Update category totals
            Long catMeetings = ((Number) categoryData.getOrDefault("totalMeetings", 0L)).longValue();
            Long catLectures = ((Number) categoryData.getOrDefault("totalLectures", 0L)).longValue();
            Long catSeminars = ((Number) categoryData.getOrDefault("totalSeminars", 0L)).longValue();

            categoryData.put("totalMeetings", catMeetings + totalMeetings);
            categoryData.put("totalLectures", catLectures + totalLectures);
            categoryData.put("totalSeminars", catSeminars + totalSeminars);
        }

        // Convert to list
        for (Map.Entry<String, Map<String, Object>> entry : categoryMap.entrySet()) {
            Map<String, Object> categoryData = entry.getValue();
            categoryData.put("categoryName", entry.getKey());
            Long totalMeetings = ((Number) categoryData.get("totalMeetings")).longValue();
            Long totalLectures = ((Number) categoryData.get("totalLectures")).longValue();
            Long totalSeminars = ((Number) categoryData.get("totalSeminars")).longValue();
            categoryData.put("total", totalMeetings + totalLectures + totalSeminars);
            categoryTotals.add(categoryData);
        }

        totals.put("categories", categoryTotals);

        // Calculate grand totals
        long grandTotalMeetings = categoryTotals.stream()
            .mapToLong(c -> ((Number) c.get("totalMeetings")).longValue())
            .sum();
        long grandTotalLectures = categoryTotals.stream()
            .mapToLong(c -> ((Number) c.get("totalLectures")).longValue())
            .sum();
        long grandTotalSeminars = categoryTotals.stream()
            .mapToLong(c -> ((Number) c.get("totalSeminars")).longValue())
            .sum();

        totals.put("grandTotalMeetings", grandTotalMeetings);
        totals.put("grandTotalLectures", grandTotalLectures);
        totals.put("grandTotalSeminars", grandTotalSeminars);
        totals.put("grandTotal", grandTotalMeetings + grandTotalLectures + grandTotalSeminars);

        return totals;
    }

    /**
     * Get all centers' monthly totals
     * الحصول على إجماليات جميع المراكز الشهرية
     */
    public Map<String, Object> getAllCentersMonthlyTotals(YearMonth yearMonth) {
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Object[]> results = statisticsRepository.aggregateAllCentersMonthlyTotals(startDate, endDate);

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("yearMonth", yearMonth.toString());
        report.put("startDate", startDate);
        report.put("endDate", endDate);

        Map<String, Map<String, Object>> centersMap = new LinkedHashMap<>();

        for (Object[] row : results) {
            String centerId = (String) row[0];
            String categoryName = (String) row[1];
            String topicName = (String) row[2];
            Long totalMeetings = ((Number) row[3]).longValue();
            Long totalLectures = ((Number) row[4]).longValue();
            Long totalSeminars = ((Number) row[5]).longValue();

            centersMap.putIfAbsent(centerId, new LinkedHashMap<>());
            Map<String, Object> centerData = centersMap.get(centerId);

            @SuppressWarnings("unchecked")
            Map<String, Map<String, Object>> categories = (Map<String, Map<String, Object>>) 
                centerData.getOrDefault("categories", new LinkedHashMap<>());

            categories.putIfAbsent(categoryName, new LinkedHashMap<>());
            Map<String, Object> categoryData = categories.get(categoryName);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> topics = (List<Map<String, Object>>) 
                categoryData.getOrDefault("topics", new ArrayList<>());

            Map<String, Object> topicData = new LinkedHashMap<>();
            topicData.put("topicName", topicName);
            topicData.put("individualMeetings", totalMeetings);
            topicData.put("lectures", totalLectures);
            topicData.put("seminars", totalSeminars);
            topicData.put("total", totalMeetings + totalLectures + totalSeminars);

            topics.add(topicData);
            categoryData.put("topics", topics);

            categories.put(categoryName, categoryData);
            centerData.put("categories", categories);
        }

        // Convert to list and add center names
        List<Map<String, Object>> centersList = new ArrayList<>();
        for (Map.Entry<String, Map<String, Object>> entry : centersMap.entrySet()) {
            Map<String, Object> centerData = entry.getValue();
            centerData.put("centerId", entry.getKey());
            
            Optional<UserProfile> profileOpt = userProfileRepository.findByUserId(entry.getKey());
            centerData.put("centerName", profileOpt.map(UserProfile::getHealthCenterName).orElse("Unknown"));
            
            centersList.add(centerData);
        }

        report.put("centers", centersList);
        return report;
    }

    /**
     * Get strategic dashboard summary
     * الحصول على ملخص لوحة التحكم الاستراتيجية
     * 
     * Returns:
     * - Total lectures and seminars for current month
     * - Ranking of centers by Maternal & Child Health activity
     * - Centers with no data in last 3 days
     */
    public Map<String, Object> getDashboardSummary(String categoryFilter) {
        YearMonth currentMonth = YearMonth.now();
        LocalDate startDate = currentMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();
        LocalDate threeDaysAgo = LocalDate.now().minusDays(3);

        Map<String, Object> summary = new LinkedHashMap<>();
        
        // Total counts for current month (all centers)
        List<Object[]> allTotals = statisticsRepository.aggregateAllCentersMonthlyTotals(startDate, endDate);
        
        long totalMeetings = 0;
        long totalLectures = 0;
        long totalSeminars = 0;
        
        for (Object[] row : allTotals) {
            totalMeetings += ((Number) row[3]).longValue();
            totalLectures += ((Number) row[4]).longValue();
            totalSeminars += ((Number) row[5]).longValue();
        }
        
        summary.put("totalMeetings", totalMeetings);
        summary.put("totalLectures", totalLectures);
        summary.put("totalSeminars", totalSeminars);
        summary.put("currentMonth", currentMonth.toString());
        
        // Ranking by category (default: Maternal & Child Health - صحة الأم والطفل)
        // Note: Category name is "صحة الأم والطفل" in the system
        String category = (categoryFilter != null && !categoryFilter.isEmpty()) 
            ? categoryFilter : "صحة الأم والطفل";
        
        List<Map<String, Object>> centerRanking = getCenterRankingByCategory(category, startDate, endDate);
        summary.put("centerRanking", centerRanking);
        summary.put("selectedCategory", category);
        
        // Inactive centers (no data in last 3 days)
        List<Map<String, Object>> inactiveCenters = getInactiveCenters(threeDaysAgo);
        summary.put("inactiveCenters", inactiveCenters);
        summary.put("inactiveCount", inactiveCenters.size());
        
        // Category statistics
        Map<String, Long> categoryStats = getCategoryStatistics(startDate, endDate);
        summary.put("categoryStatistics", categoryStats);
        
        return summary;
    }

    /**
     * Get center ranking by category activity
     * الحصول على ترتيب المراكز حسب نشاط الفئة
     */
    private List<Map<String, Object>> getCenterRankingByCategory(String categoryName, LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = statisticsRepository.aggregateAllCentersMonthlyTotals(startDate, endDate);
        
        Map<String, Long> centerTotals = new LinkedHashMap<>();
        
        for (Object[] row : results) {
            String centerId = (String) row[0];
            String category = (String) row[1];
            
            if (category.equals(categoryName)) {
                Long totalMeetings = ((Number) row[3]).longValue();
                Long totalLectures = ((Number) row[4]).longValue();
                Long totalSeminars = ((Number) row[5]).longValue();
                Long total = totalMeetings + totalLectures + totalSeminars;
                
                centerTotals.put(centerId, centerTotals.getOrDefault(centerId, 0L) + total);
            }
        }
        
        // Sort by total (descending)
        List<Map.Entry<String, Long>> sorted = new ArrayList<>(centerTotals.entrySet());
        sorted.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));
        
        // Build ranking list
        List<Map<String, Object>> ranking = new ArrayList<>();
        int rank = 1;
        for (Map.Entry<String, Long> entry : sorted) {
            Optional<UserProfile> profileOpt = userProfileRepository.findByUserId(entry.getKey());
            String centerName = profileOpt.map(UserProfile::getHealthCenterName)
                .orElse(entry.getKey());
            
            Map<String, Object> centerData = new LinkedHashMap<>();
            centerData.put("rank", rank++);
            centerData.put("centerId", entry.getKey());
            centerData.put("centerName", centerName);
            centerData.put("totalActivity", entry.getValue());
            ranking.add(centerData);
        }
        
        return ranking;
    }

    /**
     * Get inactive centers (no data in last N days)
     * الحصول على المراكز غير النشطة (لا بيانات في آخر N يوم)
     */
    private List<Map<String, Object>> getInactiveCenters(LocalDate sinceDate) {
        List<UserProfile> allCenters = userProfileRepository.findAll();
        List<Map<String, Object>> inactive = new ArrayList<>();
        
        for (UserProfile center : allCenters) {
            List<DailyStatistics> recentStats = statisticsRepository.findByCenterIdAndEntryDateBetween(
                center.getUserId(), sinceDate, LocalDate.now());
            
            if (recentStats.isEmpty()) {
                Map<String, Object> centerData = new LinkedHashMap<>();
                centerData.put("centerId", center.getUserId());
                centerData.put("centerName", center.getHealthCenterName() != null 
                    ? center.getHealthCenterName() : center.getUserId());
                centerData.put("managerName", center.getManagerName());
                centerData.put("lastActivity", center.getUpdatedAt());
                inactive.add(centerData);
            }
        }
        
        return inactive;
    }

    /**
     * Get statistics by category
     * الحصول على الإحصائيات حسب الفئة
     */
    private Map<String, Long> getCategoryStatistics(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = statisticsRepository.aggregateAllCentersMonthlyTotals(startDate, endDate);
        
        Map<String, Long> categoryTotals = new LinkedHashMap<>();
        
        for (Object[] row : results) {
            String category = (String) row[1];
            Long totalMeetings = ((Number) row[3]).longValue();
            Long totalLectures = ((Number) row[4]).longValue();
            Long totalSeminars = ((Number) row[5]).longValue();
            Long total = totalMeetings + totalLectures + totalSeminars;
            
            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0L) + total);
        }
        
        return categoryTotals;
    }

    /**
     * Calculate progress for a center in a month
     * حساب التقدم لمركز في شهر
     */
    public Map<String, Object> calculateProgress(String centerId, Integer year, Integer month) {
        YearMonth yearMonth = YearMonth.of(year, month);

        // Get actual statistics
        Map<String, Object> monthlyTotals = getMonthlyTotals(centerId, yearMonth);
        long actualMeetings = ((Number) monthlyTotals.getOrDefault("grandTotalMeetings", 0L)).longValue();
        long actualLectures = ((Number) monthlyTotals.getOrDefault("grandTotalLectures", 0L)).longValue();
        long actualSeminars = ((Number) monthlyTotals.getOrDefault("grandTotalSeminars", 0L)).longValue();
        long actualTotal = actualMeetings + actualLectures + actualSeminars;

        // Get targets
        List<MonthlyTarget> targets = targetRepository.findByCenterIdAndTargetYearAndTargetMonth(centerId, year, month);
        long targetMeetings = targets.stream().mapToLong(t -> t.getTargetMeetings() != null ? t.getTargetMeetings() : 0).sum();
        long targetLectures = targets.stream().mapToLong(t -> t.getTargetLectures() != null ? t.getTargetLectures() : 0).sum();
        long targetSeminars = targets.stream().mapToLong(t -> t.getTargetSeminars() != null ? t.getTargetSeminars() : 0).sum();
        long targetTotal = targetMeetings + targetLectures + targetSeminars;

        // Calculate percentages
        double overallPercent = targetTotal > 0 ? (actualTotal * 100.0 / targetTotal) : 0;

        // Determine status
        String status = determineProgressStatus(overallPercent, month);
        String color = getProgressColor(overallPercent);

        Map<String, Object> progress = new LinkedHashMap<>();
        progress.put("centerId", centerId);
        progress.put("year", year);
        progress.put("month", month);
        progress.put("actualTotal", actualTotal);
        progress.put("targetTotal", targetTotal);
        progress.put("overallPercent", Math.round(overallPercent * 100.0) / 100.0);
        progress.put("status", status);
        progress.put("color", color);
        progress.put("behindSchedule", status.equals("Behind Schedule"));

        return progress;
    }

    private String determineProgressStatus(double percent, int month) {
        int dayOfMonth = LocalDate.now().getDayOfMonth();
        if (dayOfMonth > 20 && percent < 50) {
            return "Behind Schedule";
        }
        if (percent >= 75) {
            return "On Track";
        } else if (percent >= 50) {
            return "Average";
        } else {
            return "Behind Schedule";
        }
    }

    private String getProgressColor(double percent) {
        if (percent >= 75) return "green";
        else if (percent >= 50) return "yellow";
        else return "red";
    }

    public Map<String, Object> getAllCentersProgress(Integer year, Integer month) {
        List<UserProfile> centers = userProfileRepository.findAll();
        List<Map<String, Object>> centersProgress = new ArrayList<>();
        double totalActual = 0;
        double totalTarget = 0;

        for (UserProfile center : centers) {
            Map<String, Object> progress = calculateProgress(center.getUserId(), year, month);
            progress.put("centerName", center.getHealthCenterName() != null ? center.getHealthCenterName() : center.getUserId());
            centersProgress.add(progress);
            totalActual += ((Number) progress.get("actualTotal")).longValue();
            totalTarget += ((Number) progress.get("targetTotal")).longValue();
        }

        double overallCompletion = totalTarget > 0 ? (totalActual * 100.0 / totalTarget) : 0;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("centersProgress", centersProgress);
        result.put("overallCompletion", Math.round(overallCompletion * 100.0) / 100.0);
        result.put("totalActual", totalActual);
        result.put("totalTarget", totalTarget);
        return result;
    }

    @Transactional
    public MonthlyTarget saveTarget(MonthlyTarget target) {
        if (!CATEGORY_TOPICS.containsKey(target.getCategoryName())) {
            throw new IllegalArgumentException("Invalid category: " + target.getCategoryName());
        }
        List<String> validTopics = CATEGORY_TOPICS.get(target.getCategoryName());
        if (!validTopics.contains(target.getTopicName())) {
            throw new IllegalArgumentException("Invalid topic: " + target.getTopicName());
        }
        Optional<MonthlyTarget> existing = targetRepository.findByCenterIdAndCategoryNameAndTopicNameAndTargetYearAndTargetMonth(
            target.getCenterId(), target.getCategoryName(), target.getTopicName(),
            target.getTargetYear(), target.getTargetMonth());
        if (existing.isPresent()) {
            MonthlyTarget existingTarget = existing.get();
            existingTarget.setTargetMeetings(target.getTargetMeetings());
            existingTarget.setTargetLectures(target.getTargetLectures());
            existingTarget.setTargetSeminars(target.getTargetSeminars());
            return targetRepository.save(existingTarget);
        }
        return targetRepository.save(target);
    }

    public List<MonthlyTarget> getTargets(String centerId, Integer year, Integer month) {
        return targetRepository.findByCenterIdAndTargetYearAndTargetMonth(centerId, year, month);
    }
}

