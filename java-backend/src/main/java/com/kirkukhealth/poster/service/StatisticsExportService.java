package com.kirkukhealth.poster.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

/**
 * Statistics Export Service
 * خدمة تصدير الإحصائيات
 * 
 * Exports monthly statistics to Excel/PDF format
 * يصدر الإحصائيات الشهرية بصيغة Excel/PDF
 */
@Service
public class StatisticsExportService {

    @Autowired
    private HealthStatisticsService statisticsService;

    /**
     * Export monthly report to Excel
     * تصدير التقرير الشهري إلى Excel
     */
    public byte[] exportMonthlyReportToExcel(YearMonth yearMonth, String centerId) throws IOException {
        Map<String, Object> monthlyData;
        final boolean isSingleCenter = (centerId != null && !centerId.isEmpty());
        
        if (isSingleCenter) {
            monthlyData = statisticsService.getMonthlyTotals(centerId, yearMonth);
        } else {
            monthlyData = statisticsService.getAllCentersMonthlyTotals(yearMonth);
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("التقرير الشهري");

        // Create styles
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle titleStyle = createTitleStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);

        int rowNum = 0;

        // Title row
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("تقرير إحصائيات الصحة الشهري - دائرة صحة كركوك – قطاع كركوك الأول");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

        // Month info
        Row monthRow = sheet.createRow(rowNum++);
        Cell monthCell = monthRow.createCell(0);
        monthCell.setCellValue("الشهر: " + yearMonth.toString());
        monthCell.setCellStyle(dataStyle);

        rowNum++; // Empty row

        // Table header
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"الفئة", "الموضوع", "اللقاءات الفردية", "المحاضرات", "الندوات", "الإجمالي"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Data rows
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> categories = (List<Map<String, Object>>) monthlyData.get("categories");
        
        if (isSingleCenter && categories != null) {
            for (Map<String, Object> category : categories) {
                String categoryName = (String) category.get("categoryName");
                
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> topics = (List<Map<String, Object>>) category.get("topics");
                
                if (topics != null) {
                    for (Map<String, Object> topic : topics) {
                        Row dataRow = sheet.createRow(rowNum++);
                        
                        dataRow.createCell(0).setCellValue(categoryName);
                        dataRow.createCell(1).setCellValue((String) topic.get("topicName"));
                        dataRow.createCell(2).setCellValue(((Number) topic.get("individualMeetings")).longValue());
                        dataRow.createCell(3).setCellValue(((Number) topic.get("lectures")).longValue());
                        dataRow.createCell(4).setCellValue(((Number) topic.get("seminars")).longValue());
                        dataRow.createCell(5).setCellValue(((Number) topic.get("total")).longValue());
                        
                        for (int i = 0; i < 6; i++) {
                            dataRow.getCell(i).setCellStyle(dataStyle);
                        }
                    }
                }
                
                // Category total row
                Row totalRow = sheet.createRow(rowNum++);
                Cell totalLabelCell = totalRow.createCell(0);
                totalLabelCell.setCellValue("إجمالي " + categoryName);
                totalLabelCell.setCellStyle(headerStyle);
                
                totalRow.createCell(2).setCellValue(((Number) category.get("totalMeetings")).longValue());
                totalRow.createCell(3).setCellValue(((Number) category.get("totalLectures")).longValue());
                totalRow.createCell(4).setCellValue(((Number) category.get("totalSeminars")).longValue());
                totalRow.createCell(5).setCellValue(((Number) category.get("total")).longValue());
                
                for (int i = 0; i < 6; i++) {
                    if (totalRow.getCell(i) != null) {
                        totalRow.getCell(i).setCellStyle(headerStyle);
                    }
                }
                
                rowNum++; // Empty row
            }
        } else if (!isSingleCenter) {
            // Handle all centers report
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> centers = (List<Map<String, Object>>) monthlyData.get("centers");
            
            if (centers != null) {
                for (Map<String, Object> center : centers) {
                    // Center header
                    Row centerHeaderRow = sheet.createRow(rowNum++);
                    Cell centerHeaderCell = centerHeaderRow.createCell(0);
                    centerHeaderCell.setCellValue("المركز: " + center.get("centerName"));
                    centerHeaderCell.setCellStyle(titleStyle);
                    sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 5));
                    
                    // Center categories
                    @SuppressWarnings("unchecked")
                    Map<String, Map<String, Object>> centerCategories = 
                        (Map<String, Map<String, Object>>) center.get("categories");
                    
                    if (centerCategories != null) {
                        for (Map.Entry<String, Map<String, Object>> catEntry : centerCategories.entrySet()) {
                            String categoryName = catEntry.getKey();
                            Map<String, Object> category = catEntry.getValue();
                            
                            @SuppressWarnings("unchecked")
                            List<Map<String, Object>> topics = (List<Map<String, Object>>) category.get("topics");
                            
                            if (topics != null) {
                                for (Map<String, Object> topic : topics) {
                                    Row dataRow = sheet.createRow(rowNum++);
                                    
                                    dataRow.createCell(0).setCellValue(categoryName);
                                    dataRow.createCell(1).setCellValue((String) topic.get("topicName"));
                                    dataRow.createCell(2).setCellValue(((Number) topic.get("individualMeetings")).longValue());
                                    dataRow.createCell(3).setCellValue(((Number) topic.get("lectures")).longValue());
                                    dataRow.createCell(4).setCellValue(((Number) topic.get("seminars")).longValue());
                                    dataRow.createCell(5).setCellValue(((Number) topic.get("total")).longValue());
                                    
                                    for (int i = 0; i < 6; i++) {
                                        dataRow.getCell(i).setCellStyle(dataStyle);
                                    }
                                }
                            }
                        }
                    }
                    
                    rowNum++; // Empty row between centers
                }
            }
        }

        // Grand total row (only for single center)
        if (isSingleCenter && monthlyData.containsKey("grandTotal")) {
            Row grandTotalRow = sheet.createRow(rowNum++);
            Cell grandTotalLabelCell = grandTotalRow.createCell(0);
            grandTotalLabelCell.setCellValue("الإجمالي الكلي");
            grandTotalLabelCell.setCellStyle(titleStyle);
            
            grandTotalRow.createCell(2).setCellValue(((Number) monthlyData.get("grandTotalMeetings")).longValue());
            grandTotalRow.createCell(3).setCellValue(((Number) monthlyData.get("grandTotalLectures")).longValue());
            grandTotalRow.createCell(4).setCellValue(((Number) monthlyData.get("grandTotalSeminars")).longValue());
            grandTotalRow.createCell(5).setCellValue(((Number) monthlyData.get("grandTotal")).longValue());
            
            for (int i = 0; i < 6; i++) {
                if (grandTotalRow.getCell(i) != null) {
                    grandTotalRow.getCell(i).setCellStyle(titleStyle);
                }
            }
        }

        // Auto-size columns
        for (int i = 0; i < 6; i++) {
            sheet.autoSizeColumn(i);
        }

        // Convert to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();
        
        return baos.toByteArray();
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
}

