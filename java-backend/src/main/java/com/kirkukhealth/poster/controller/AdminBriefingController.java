package com.kirkukhealth.poster.controller;

import com.kirkukhealth.poster.service.DailyBriefingService;
import com.kirkukhealth.poster.service.PdfReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

/**
 * Admin Briefing Controller
 * متحكم الإحاطة اليومية للمدير
 * 
 * Provides daily briefing data for Admin
 * يوفر بيانات الإحاطة اليومية للمدير
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminBriefingController {

    @Autowired
    private DailyBriefingService briefingService;

    @Autowired
    private PdfReportService pdfReportService;

    @Autowired
    private com.kirkukhealth.poster.service.GoogleDriveService googleDriveService;

    /**
     * Get daily briefing
     * الحصول على الإحاطة اليومية
     * 
     * GET /api/admin/daily-brief
     */
    @GetMapping("/daily-brief")
    public ResponseEntity<Map<String, Object>> getDailyBriefing() {
        Map<String, Object> briefing = briefingService.getDailyBriefing();
        return ResponseEntity.ok(briefing);
    }

    /**
     * Generate weekly PDF report
     * توليد تقرير PDF أسبوعي
     * 
     * GET /api/admin/report/weekly-pdf?startDate={startDate}&endDate={endDate}&userId={userId}
     */
    @GetMapping("/report/weekly-pdf")
    public ResponseEntity<byte[]> generateWeeklyPdfReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String userId) {
        
        try {
            // Default to last 7 days if not specified
            LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();
            LocalDate start = startDate != null ? LocalDate.parse(startDate) : end.minusDays(7);

            byte[] pdfBytes = pdfReportService.generateWeeklyReport(start, end);

            // Generate filename
            String filename = String.format("Sector1_Report_%s.pdf", 
                LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy_MM_dd")));

            // Upload to user's Google Drive if linked
            if (userId != null && !userId.isEmpty()) {
                try {
                    googleDriveService.uploadPdfToDrive(userId, pdfBytes, filename);
                } catch (Exception e) {
                    // Log error but don't fail the request
                    System.err.println("Failed to upload to Google Drive for user " + userId + ": " + e.getMessage());
                }
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(pdfBytes.length);

            return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(("Error generating PDF: " + e.getMessage()).getBytes());
        }
    }
}

