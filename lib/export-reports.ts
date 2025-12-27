/**
 * Report Export Utilities
 * 
 * Export activity logs and analytics to Excel and PDF formats
 */

import * as XLSX from 'xlsx'
import jsPDF from 'jspdf'
import autoTable from 'jspdf-autotable'
import { format } from 'date-fns'
import { ar } from 'date-fns/locale'

export interface ActivityLog {
  id: string
  action: string
  details: string
  userName: string
  userRole: string
  createdAt: string
}

/**
 * Export activity logs to Excel
 */
export function exportLogsToExcel(logs: ActivityLog[], filename?: string): void {
  const data = logs.map((log) => ({
    'التاريخ والوقت': format(new Date(log.createdAt), 'yyyy-MM-dd HH:mm:ss', { locale: ar }),
    'الإجراء': log.action,
    'التفاصيل': log.details,
    'المستخدم': log.userName,
    'الصلاحية': log.userRole,
  }))

  const ws = XLSX.utils.json_to_sheet(data)
  const wb = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(wb, ws, 'سجل الأنشطة')

  // Auto-size columns
  const colWidths = [
    { wch: 20 }, // التاريخ
    { wch: 20 }, // الإجراء
    { wch: 50 }, // التفاصيل
    { wch: 20 }, // المستخدم
    { wch: 15 }, // الصلاحية
  ]
  ws['!cols'] = colWidths

  XLSX.writeFile(wb, filename || `activity-logs-${format(new Date(), 'yyyy-MM-dd')}.xlsx`)
}

/**
 * Export activity logs to PDF
 */
export function exportLogsToPDF(logs: ActivityLog[], title?: string): void {
  const doc = new jsPDF('l', 'mm', 'a4') // Landscape for better table fit
  
  // Header
  doc.setFontSize(18)
  doc.setTextColor(6, 182, 212) // Primary color
  doc.text(title || 'تقرير سجل الأنشطة', 14, 15)
  
  doc.setFontSize(10)
  doc.setTextColor(100, 100, 100)
  doc.text(`دائرة صحة كركوك – قطاع كركوك الأول – وحدة تعزيز الصحة`, 14, 22)
  doc.text(`تاريخ التصدير: ${format(new Date(), 'yyyy-MM-dd', { locale: ar })}`, 14, 27)
  
  // Table data
  const tableData = logs.map((log) => [
    format(new Date(log.createdAt), 'yyyy-MM-dd HH:mm', { locale: ar }),
    log.action,
    log.details.substring(0, 40) + (log.details.length > 40 ? '...' : ''),
    log.userName,
    log.userRole,
  ])
  
  autoTable(doc, {
    head: [['التاريخ', 'الإجراء', 'التفاصيل', 'المستخدم', 'الصلاحية']],
    body: tableData,
    startY: 35,
    styles: {
      font: 'helvetica',
      fontSize: 8,
      cellPadding: 2,
      textColor: [50, 50, 50],
    },
    headStyles: {
      fillColor: [6, 182, 212], // Primary color
      textColor: [255, 255, 255],
      fontStyle: 'bold',
    },
    alternateRowStyles: {
      fillColor: [245, 247, 250],
    },
    margin: { top: 35, right: 14, bottom: 20, left: 14 },
  })
  
  // Footer
  const pageCount = doc.getNumberOfPages()
  for (let i = 1; i <= pageCount; i++) {
    doc.setPage(i)
    doc.setFontSize(8)
    doc.setTextColor(150, 150, 150)
    doc.text(
      `صفحة ${i} من ${pageCount}`,
      doc.internal.pageSize.getWidth() / 2,
      doc.internal.pageSize.getHeight() - 10,
      { align: 'center' }
    )
  }
  
  doc.save(`activity-logs-${format(new Date(), 'yyyy-MM-dd')}.pdf`)
}

/**
 * Export monthly performance report
 */
export function exportMonthlyReport(
  logs: ActivityLog[],
  stats: {
    totalPosters: number
    activeUsers: number
    aiGenerated: number
  }
): void {
  const doc = new jsPDF('p', 'mm', 'a4')
  
  // Cover page
  doc.setFillColor(6, 182, 212)
  doc.rect(0, 0, 210, 297, 'F')
  
  doc.setTextColor(255, 255, 255)
  doc.setFontSize(24)
  doc.text('تقرير الأداء الشهري', 105, 100, { align: 'center' })
  
  doc.setFontSize(16)
  doc.text('دائرة صحة كركوك', 105, 120, { align: 'center' })
  doc.text('قطاع كركوك الأول – وحدة تعزيز الصحة', 105, 135, { align: 'center' })
  
  doc.setFontSize(12)
  doc.text(`شهر: ${format(new Date(), 'MMMM yyyy', { locale: ar })}`, 105, 160, { align: 'center' })
  doc.text(`تاريخ التصدير: ${format(new Date(), 'yyyy-MM-dd', { locale: ar })}`, 105, 175, { align: 'center' })
  
  // Stats page
  doc.addPage()
  doc.setFillColor(255, 255, 255)
  doc.setTextColor(50, 50, 50)
  
  doc.setFontSize(18)
  doc.text('ملخص الأداء', 14, 20)
  
  doc.setFontSize(12)
  const statsY = 40
  doc.text(`إجمالي البوسترات: ${stats.totalPosters}`, 14, statsY)
  doc.text(`المستخدمين النشطين: ${stats.activeUsers}`, 14, statsY + 10)
  doc.text(`البوسترات المولدة بالذكاء الاصطناعي: ${stats.aiGenerated}`, 14, statsY + 20)
  
  // Activity logs table
  if (logs.length > 0) {
    const tableData = logs.slice(0, 50).map((log) => [
      format(new Date(log.createdAt), 'yyyy-MM-dd HH:mm', { locale: ar }),
      log.action,
      log.userName,
    ])
    
    autoTable(doc, {
      head: [['التاريخ', 'الإجراء', 'المستخدم']],
      body: tableData,
      startY: statsY + 35,
      styles: {
        font: 'helvetica',
        fontSize: 9,
      },
      headStyles: {
        fillColor: [6, 182, 212],
        textColor: [255, 255, 255],
      },
    })
  }
  
  doc.save(`monthly-report-${format(new Date(), 'yyyy-MM')}.pdf`)
}

