'use client'

import { useEffect, useState } from 'react'
import { useSession } from 'next-auth/react'
import { useRouter } from 'next/navigation'
import { motion } from 'framer-motion'
import { FileSpreadsheet, FileText, Activity, TrendingUp, Clock } from 'lucide-react'
import Navbar from '@/components/Navbar'
import Card from '@/components/ui/Card'
import Button from '@/components/ui/Button'
import DataTable from '@/components/ui/DataTable'
import Badge from '@/components/ui/Badge'
import { exportLogsToExcel, exportLogsToPDF } from '@/lib/export-reports'

interface ActivityLog {
  id: string
  action: string
  details: string
  userName: string
  userRole: string
  createdAt: string
}

interface LogsResponse {
  logs: ActivityLog[]
  pagination: {
    page: number
    limit: number
    total: number
    totalPages: number
  }
}

export default function ActivityLogsPage() {
  const { data: session, status } = useSession()
  const router = useRouter()
  const [logs, setLogs] = useState<ActivityLog[]>([])
  const [loading, setLoading] = useState(true)
  const [pagination, setPagination] = useState({
    page: 1,
    limit: 50,
    total: 0,
    totalPages: 0,
  })

  useEffect(() => {
    if (status === 'loading') return

    if (!session) {
      router.push('/login')
      return
    }

    const role = (session.user as any).role
    if (role !== 'ADMIN' && role !== 'SUPER_ADMIN') {
      router.push('/dashboard/user')
      return
    }

    fetchLogs()
  }, [session, status, router])

  const fetchLogs = async (page: number = 1) => {
    try {
      setLoading(true)
      const res = await fetch(`/api/activity-logs?page=${page}&limit=${pagination.limit}`)
      
      if (res.ok) {
        const data: LogsResponse = await res.json()
        setLogs(data.logs)
        setPagination(data.pagination)
      } else {
        console.error('Failed to fetch logs')
      }
    } catch (error) {
      console.error('Error fetching logs:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleExportExcel = () => {
    exportLogsToExcel(logs, `activity-logs-${new Date().toISOString().split('T')[0]}.xlsx`)
  }

  const handleExportPDF = () => {
    exportLogsToPDF(logs, 'تقرير سجل الأنشطة')
  }

  const getActionBadgeVariant = (action: string): 'success' | 'error' | 'warning' | 'info' | 'default' => {
    if (action.includes('Error')) return 'error'
    if (action.includes('Login')) return 'success'
    if (action.includes('Generated')) return 'info'
    return 'default'
  }

  const getRoleBadgeVariant = (role: string): 'success' | 'error' | 'warning' | 'info' | 'default' => {
    if (role === 'ADMIN' || role === 'SUPER_ADMIN') return 'info'
    return 'default'
  }

  // Calculate stats
  const stats = {
    total: logs.length,
    errors: logs.filter(log => log.action.includes('Error')).length,
    logins: logs.filter(log => log.action.includes('Login')).length,
    generated: logs.filter(log => log.action.includes('Generated')).length,
  }

  if (loading || status === 'loading') {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-primary-50 via-sky-50 to-white">
        <motion.div
          initial={{ opacity: 0, scale: 0.9 }}
          animate={{ opacity: 1, scale: 1 }}
          className="text-center"
        >
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto mb-4"></div>
          <p className="text-gray-600">جاري التحميل...</p>
        </motion.div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-primary-50 via-sky-50 to-white">
      <Navbar />
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <motion.div
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          className="mb-8"
        >
          <h1 className="text-4xl font-bold bg-gradient-to-r from-primary-600 to-sky-600 bg-clip-text text-transparent mb-2">
            سجل الأنشطة
          </h1>
          <p className="text-gray-600 text-lg">
            تتبع جميع الأنشطة والأحداث في النظام
          </p>
        </motion.div>

        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <Card delay={0.1}>
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 mb-1">إجمالي السجلات</p>
                <p className="text-2xl font-bold text-gray-900">{stats.total}</p>
              </div>
              <div className="p-3 bg-primary-100 rounded-lg">
                <Activity className="w-6 h-6 text-primary-600" />
              </div>
            </div>
          </Card>

          <Card delay={0.2}>
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 mb-1">تسجيلات الدخول</p>
                <p className="text-2xl font-bold text-green-600">{stats.logins}</p>
              </div>
              <div className="p-3 bg-green-100 rounded-lg">
                <TrendingUp className="w-6 h-6 text-green-600" />
              </div>
            </div>
          </Card>

          <Card delay={0.3}>
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 mb-1">البوسترات المولدة</p>
                <p className="text-2xl font-bold text-blue-600">{stats.generated}</p>
              </div>
              <div className="p-3 bg-blue-100 rounded-lg">
                <FileText className="w-6 h-6 text-blue-600" />
              </div>
            </div>
          </Card>

          <Card delay={0.4}>
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 mb-1">الأخطاء</p>
                <p className="text-2xl font-bold text-red-600">{stats.errors}</p>
              </div>
              <div className="p-3 bg-red-100 rounded-lg">
                <Activity className="w-6 h-6 text-red-600" />
              </div>
            </div>
          </Card>
        </div>

        {/* Data Table */}
        <Card>
          <DataTable
            data={logs}
            columns={[
              {
                header: 'التاريخ والوقت',
                accessor: 'createdAt',
                render: (value) => (
                  <div className="flex items-center gap-2">
                    <Clock className="w-4 h-4 text-gray-400" />
                    <span className="text-sm text-gray-700">
                      {new Date(value).toLocaleString('ar-EG', {
                        year: 'numeric',
                        month: 'long',
                        day: 'numeric',
                        hour: '2-digit',
                        minute: '2-digit',
                      })}
                    </span>
                  </div>
                ),
              },
              {
                header: 'الإجراء',
                accessor: 'action',
                render: (value) => (
                  <Badge variant={getActionBadgeVariant(value)}>
                    {value}
                  </Badge>
                ),
              },
              {
                header: 'التفاصيل',
                accessor: 'details',
                render: (value) => (
                  <div className="max-w-md">
                    <p className="text-sm text-gray-900 truncate" title={value}>
                      {value}
                    </p>
                  </div>
                ),
              },
              {
                header: 'المستخدم',
                accessor: 'userName',
              },
              {
                header: 'الصلاحية',
                accessor: 'userRole',
                render: (value) => (
                  <Badge variant={getRoleBadgeVariant(value)}>
                    {value}
                  </Badge>
                ),
              },
            ]}
            searchable
            exportable
            onExportExcel={handleExportExcel}
            onExportPDF={handleExportPDF}
          />
        </Card>

        {/* Pagination */}
        {pagination.totalPages > 1 && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            className="mt-6 flex justify-center"
          >
            <div className="flex gap-2">
              <Button
                variant="outline"
                size="sm"
                onClick={() => fetchLogs(pagination.page - 1)}
                disabled={pagination.page === 1}
              >
                السابق
              </Button>
              <span className="px-4 py-2 text-sm text-gray-700 flex items-center">
                صفحة {pagination.page} من {pagination.totalPages}
              </span>
              <Button
                variant="outline"
                size="sm"
                onClick={() => fetchLogs(pagination.page + 1)}
                disabled={pagination.page === pagination.totalPages}
              >
                التالي
              </Button>
            </div>
          </motion.div>
        )}
      </div>
    </div>
  )
}
