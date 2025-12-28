package com.kirkukhealth.poster.repository;

import com.kirkukhealth.poster.model.FAQEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for FAQ operations
 * مستودع عمليات الأسئلة الشائعة
 */
@Repository
public interface FAQRepository extends JpaRepository<FAQEntry, String> {
    
    /**
     * Find featured FAQ entries
     * البحث عن الإدخالات المميزة
     */
    List<FAQEntry> findByIsFeaturedTrueOrderByViewCountDesc();
    
    /**
     * Find FAQ entries by category
     * البحث عن الإدخالات حسب الفئة
     */
    List<FAQEntry> findByCategoryOrderByViewCountDesc(String category);
    
    /**
     * Search FAQ entries by question or answer
     * البحث عن الإدخالات حسب السؤال أو الجواب
     */
    @Query("SELECT f FROM FAQEntry f WHERE " +
           "LOWER(f.question) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(f.answer) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(f.tags) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "ORDER BY f.viewCount DESC")
    List<FAQEntry> search(@Param("query") String query);
    
    /**
     * Find all categories
     * البحث عن جميع الفئات
     */
    @Query("SELECT DISTINCT f.category FROM FAQEntry f WHERE f.category IS NOT NULL")
    List<String> findAllCategories();
}

