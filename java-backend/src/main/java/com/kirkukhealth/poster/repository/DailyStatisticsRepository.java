package com.kirkukhealth.poster.repository;

import com.kirkukhealth.poster.model.DailyStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for Daily Statistics operations
 * مستودع عمليات الإحصائيات اليومية
 */
@Repository
public interface DailyStatisticsRepository extends JpaRepository<DailyStatistics, String> {
    
    /**
     * Find statistics by center ID
     * البحث عن الإحصائيات حسب معرف المركز
     */
    List<DailyStatistics> findByCenterId(String centerId);
    
    /**
     * Find statistics by center ID and date range
     * البحث عن الإحصائيات حسب معرف المركز ونطاق التاريخ
     */
    List<DailyStatistics> findByCenterIdAndEntryDateBetween(
        String centerId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Find statistics by date range
     * البحث عن الإحصائيات حسب نطاق التاريخ
     */
    List<DailyStatistics> findByEntryDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find statistics by center, category, and date range
     * البحث عن الإحصائيات حسب المركز والفئة ونطاق التاريخ
     */
    List<DailyStatistics> findByCenterIdAndCategoryNameAndEntryDateBetween(
        String centerId, String categoryName, LocalDate startDate, LocalDate endDate);
    
    /**
     * Aggregate monthly totals for a center
     * تجميع الإجماليات الشهرية لمركز
     */
    @Query("SELECT d.categoryName, d.topicName, " +
           "SUM(d.individualMeetings) as totalMeetings, " +
           "SUM(d.lectures) as totalLectures, " +
           "SUM(d.seminars) as totalSeminars " +
           "FROM DailyStatistics d " +
           "WHERE d.centerId = :centerId " +
           "AND d.entryDate >= :startDate AND d.entryDate <= :endDate " +
           "GROUP BY d.categoryName, d.topicName " +
           "ORDER BY d.categoryName, d.topicName")
    List<Object[]> aggregateMonthlyTotals(
        @Param("centerId") String centerId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);
    
    /**
     * Get all centers' monthly totals
     * الحصول على إجماليات جميع المراكز الشهرية
     */
    @Query("SELECT d.centerId, d.categoryName, d.topicName, " +
           "SUM(d.individualMeetings) as totalMeetings, " +
           "SUM(d.lectures) as totalLectures, " +
           "SUM(d.seminars) as totalSeminars " +
           "FROM DailyStatistics d " +
           "WHERE d.entryDate >= :startDate AND d.entryDate <= :endDate " +
           "GROUP BY d.centerId, d.categoryName, d.topicName " +
           "ORDER BY d.centerId, d.categoryName, d.topicName")
    List<Object[]> aggregateAllCentersMonthlyTotals(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);
}

