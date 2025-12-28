package com.kirkukhealth.poster.controller;

import com.kirkukhealth.poster.model.UserProfile;
import com.kirkukhealth.poster.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Admin Controller for Statistics & Reporting
 * متحكم الإدارة للإحصائيات والتقارير
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private UserProfileService userProfileService;

    /**
     * Get statistics for all 23 health centers
     * الحصول على إحصائيات جميع المراكز الصحية الـ 23
     * 
     * GET /api/admin/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        try {
            List<UserProfile> centers = userProfileService.getAllHealthCentersWithStats();
            
            // Calculate total posters
            int totalPosters = centers.stream()
                .mapToInt(center -> center.getPostersGeneratedCount() != null ? center.getPostersGeneratedCount() : 0)
                .sum();
            
            // Build statistics response
            List<Map<String, Object>> centerStats = centers.stream()
                .map(center -> {
                    Map<String, Object> stats = new HashMap<>();
                    stats.put("centerId", center.getUserId());
                    stats.put("healthCenterName", center.getHealthCenterName());
                    stats.put("managerName", center.getManagerName());
                    stats.put("postersGeneratedCount", center.getPostersGeneratedCount() != null ? center.getPostersGeneratedCount() : 0);
                    return stats;
                })
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("totalCenters", centers.size());
            response.put("totalPostersGenerated", totalPosters);
            response.put("centers", centerStats);
            response.put("message", "إحصائيات المراكز الصحية الـ 23");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "خطأ في جلب الإحصائيات");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Export statistics as Official CSV Report
     * تصدير الإحصائيات كتقرير CSV رسمي
     * 
     * GET /api/admin/statistics/export
     * 
     * Features:
     * - Official header with Directorate name and export date/time
     * - Includes: Center ID, Center Name, Manager Name, Total Posters, Last Activity
     * - Ready for presentation to Directorate
     */
    @GetMapping("/statistics/export")
    public ResponseEntity<byte[]> exportStatisticsAsCSV() {
        try {
            List<UserProfile> centers = userProfileService.getAllHealthCentersWithStats();
            
            // Create CSV content
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
            
            // Write BOM for Excel UTF-8 support
            baos.write(0xEF);
            baos.write(0xBB);
            baos.write(0xBF);
            
            // Write Official Header
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String exportDateTime = now.format(formatter);
            
            writer.write("Official Activity Report - Kirkuk Health Directorate - First Sector\n");
            writer.write("تقرير النشاط الرسمي - دائرة صحة كركوك – قطاع كركوك الأول\n");
            writer.write("Export Date and Time: " + exportDateTime + "\n");
            writer.write("تاريخ ووقت التصدير: " + exportDateTime + "\n");
            writer.write("\n"); // Empty line
            
            // Write CSV column headers
            writer.write("Center ID,Health Center Name,Manager Name,Total Posters Generated,Last Activity Timestamp\n");
            
            // Write data rows
            for (UserProfile center : centers) {
                String centerId = center.getUserId() != null ? center.getUserId() : "";
                String centerName = center.getHealthCenterName() != null ? escapeCSV(center.getHealthCenterName()) : "";
                String managerName = center.getManagerName() != null ? escapeCSV(center.getManagerName()) : "";
                int count = center.getPostersGeneratedCount() != null ? center.getPostersGeneratedCount() : 0;
                
                // Last Activity Timestamp (use updated_at, fallback to created_at)
                String lastActivity = "";
                if (center.getUpdatedAt() != null) {
                    lastActivity = center.getUpdatedAt().format(formatter);
                } else if (center.getCreatedAt() != null) {
                    lastActivity = center.getCreatedAt().format(formatter);
                } else {
                    lastActivity = "N/A";
                }
                
                writer.write(String.format("%s,%s,%s,%d,%s\n", 
                    centerId, centerName, managerName, count, lastActivity));
            }
            
            // Write footer summary
            int totalPosters = centers.stream()
                .mapToInt(c -> c.getPostersGeneratedCount() != null ? c.getPostersGeneratedCount() : 0)
                .sum();
            
            writer.write("\n"); // Empty line
            writer.write("Summary,Total Centers: " + centers.size() + ",Total Posters Generated: " + totalPosters + "\n");
            writer.write("الملخص,إجمالي المراكز: " + centers.size() + ",إجمالي البوسترات المولدة: " + totalPosters + "\n");
            
            writer.flush();
            byte[] csvBytes = baos.toByteArray();
            writer.close();
            
            // Set response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
            headers.setContentDispositionFormData("attachment", "official-activity-report-" + 
                java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".csv");
            headers.setContentLength(csvBytes.length);
            
            System.out.println("✅ Official CSV report exported: " + centers.size() + " centers, " + totalPosters + " total posters");
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(csvBytes);
            
        } catch (IOException e) {
            System.err.println("❌ Error exporting statistics: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(("Error exporting statistics: " + e.getMessage()).getBytes());
        } catch (Exception e) {
            System.err.println("❌ Unexpected error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(("Unexpected error: " + e.getMessage()).getBytes());
        }
    }

    /**
     * Escape CSV special characters
     * تهريب الأحرف الخاصة في CSV
     */
    private String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        // If value contains comma, quote, or newline, wrap in quotes and escape quotes
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}

