package com.kirkukhealth.poster.repository;

import com.kirkukhealth.poster.model.HealthOccasion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for HealthOccasion operations
 * مستودع عمليات المناسبات الصحية
 */
@Repository
public interface HealthOccasionRepository extends JpaRepository<HealthOccasion, String> {

    /**
     * Find active occasions for a specific date
     * البحث عن المناسبات النشطة لتاريخ محدد
     */
    @Query("SELECT h FROM HealthOccasion h WHERE h.isActive = true " +
           "AND ((h.isWeekLong = false AND h.occasionDate = :date) OR " +
           "(h.isWeekLong = true AND :date >= h.occasionDate AND :date <= h.endDate))")
    List<HealthOccasion> findActiveOccasionsForDate(@Param("date") LocalDate date);

    /**
     * Find occasions in a date range
     * البحث عن المناسبات في نطاق تاريخي
     */
    @Query("SELECT h FROM HealthOccasion h WHERE h.isActive = true " +
           "AND ((h.isWeekLong = false AND h.occasionDate BETWEEN :startDate AND :endDate) OR " +
           "(h.isWeekLong = true AND h.occasionDate <= :endDate AND h.endDate >= :startDate))")
    List<HealthOccasion> findOccasionsInRange(@Param("startDate") LocalDate startDate, 
                                               @Param("endDate") LocalDate endDate);

    /**
     * Find occasion by name (Arabic or English)
     * البحث عن مناسبة بالاسم (عربي أو إنجليزي)
     */
    Optional<HealthOccasion> findByOccasionNameArOrOccasionNameEn(String nameAr, String nameEn);

    /**
     * Find all active occasions
     * البحث عن جميع المناسبات النشطة
     */
    List<HealthOccasion> findByIsActiveTrueOrderByOccasionDateAsc();
}


