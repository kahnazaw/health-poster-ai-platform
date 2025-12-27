'use client'

import { motion } from 'framer-motion'
import { Search, Download, FileSpreadsheet, FileText } from 'lucide-react'
import { useState, useMemo } from 'react'
import Button from './Button'

interface Column {
  header: string
  accessor: string
  render?: (value: any, row: any) => React.ReactNode
}

interface DataTableProps {
  data: any[]
  columns: Column[]
  searchable?: boolean
  exportable?: boolean
  onExportExcel?: () => void
  onExportPDF?: () => void
  title?: string
}

export default function DataTable({
  data,
  columns,
  searchable = true,
  exportable = false,
  onExportExcel,
  onExportPDF,
  title,
}: DataTableProps) {
  const [searchTerm, setSearchTerm] = useState('')
  
  const filteredData = useMemo(() => {
    if (!searchTerm) return data
    
    return data.filter((row) =>
      columns.some((col) => {
        const value = row[col.accessor]
        return value?.toString().toLowerCase().includes(searchTerm.toLowerCase())
      })
    )
  }, [data, searchTerm, columns])
  
  return (
    <div className="space-y-4">
      {/* Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        {title && (
          <h3 className="text-xl font-bold text-gray-900">{title}</h3>
        )}
        <div className="flex flex-wrap gap-2 w-full sm:w-auto">
          {searchable && (
            <div className="relative flex-1 sm:flex-initial min-w-[200px]">
              <Search className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
              <input
                type="text"
                placeholder="بحث..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
            </div>
          )}
          {exportable && (
            <>
              {onExportExcel && (
                <Button
                  variant="outline"
                  size="sm"
                  icon={FileSpreadsheet}
                  onClick={onExportExcel}
                >
                  Excel
                </Button>
              )}
              {onExportPDF && (
                <Button
                  variant="outline"
                  size="sm"
                  icon={FileText}
                  onClick={onExportPDF}
                >
                  PDF
                </Button>
              )}
            </>
          )}
        </div>
      </div>
      
      {/* Table */}
      <div className="overflow-x-auto rounded-lg border border-gray-200">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gradient-to-r from-primary-50 to-sky-50">
            <tr>
              {columns.map((col, idx) => (
                <th
                  key={idx}
                  className="px-6 py-3 text-right text-xs font-semibold text-gray-700 uppercase tracking-wider"
                >
                  {col.header}
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {filteredData.length === 0 ? (
              <tr>
                <td colSpan={columns.length} className="px-6 py-8 text-center text-gray-500">
                  لا توجد بيانات
                </td>
              </tr>
            ) : (
              filteredData.map((row, rowIdx) => (
                <motion.tr
                  key={rowIdx}
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  transition={{ delay: rowIdx * 0.05 }}
                  className="hover:bg-gray-50 transition-colors"
                >
                  {columns.map((col, colIdx) => (
                    <td key={colIdx} className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {col.render ? col.render(row[col.accessor], row) : row[col.accessor]}
                    </td>
                  ))}
                </motion.tr>
              ))
            )}
          </tbody>
        </table>
      </div>
      
      {/* Results count */}
      {searchable && searchTerm && (
        <p className="text-sm text-gray-600">
          تم العثور على {filteredData.length} من {data.length} سجل
        </p>
      )}
    </div>
  )
}

