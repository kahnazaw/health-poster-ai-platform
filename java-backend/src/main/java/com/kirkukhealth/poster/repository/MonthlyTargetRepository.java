package com.kirkukhealth.poster.repository;

import com.kirkukhealth.poster.model.MonthlyTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Monthly Target operations
 * مستودع عمليات الأهداف الشهرية
 */
@Repository
public interface MonthlyTargetRepository extends JpaRepository<MonthlyTarget, String> {
    
    /**
     * Find targets by center ID and month/year
     * البحث عن الأهداف حسب معرف المركز والشهر/السنة
     */
    List<MonthlyTarget> findByCenterIdAndTargetYearAndTargetMonth(
        String centerId, Integer targetYear, Integer targetMonth);
    
    /**
     * Find target by center, category, topic, month, and year
     * البحث عن هدف محدد
     */
    Optional<MonthlyTarget> findByCenterIdAndCategoryNameAndTopicNameAndTargetYearAndTargetMonth(
        String centerId, String categoryName, String topicName, Integer targetYear, Integer targetMonth);
    
    /**
     * Find all targets for a specific month/year
     * البحث عن جميع الأهداف لشهر/سنة معينة
     */
    List<MonthlyTarget> findByTargetYearAndTargetMonth(Integer targetYear, Integer targetMonth);
    
    /**
     * Get total targets for a center in a month
     * الحصول على إجمالي الأهداف لمركز في شهر
     */
    @Query("SELECT SUM(m.targetMeetings + m.targetLectures + m.targetSeminars) " +
           "FROM MonthlyTarget m " +
           "WHERE m.centerId = :centerId AND m.targetYear = :year AND m.targetMonth = :month")
    Long getTotalTargetForCenter(@Param("centerId") String centerId, 
                                  @Param("year") Integer year, 
                                  @Param("month") Integer month);
}

