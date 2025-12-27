'use client'

import { useEffect, useState } from 'react'
import { useSession } from 'next-auth/react'
import { useRouter } from 'next/navigation'
import Navbar from '@/components/Navbar'
import {
  BarChart,
  Bar,
  LineChart,
  Line,
  PieChart,
  Pie,
  Cell,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts'

interface AnalyticsData {
  overview: {
    totalPosters: number
    approvedPosters: number
    draftPosters: number
    underReviewPosters: number
    rejectedPosters: number
    totalDownloads: number
    totalPrints: number
    aiGeneratedCount: number
    totalUsers: number
    totalTemplates: number
    recentPosters: number
  }
  statusDistribution: {
    approved: number
    draft: number
    underReview: number
    rejected: number
  }
}

const COLORS = ['#0ea5e9', '#22c55e', '#f59e0b', '#ef4444']

export default function AnalyticsPage() {
  const { data: session, status } = useSession()
  const router = useRouter()
  const [analytics, setAnalytics] = useState<AnalyticsData | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (status === 'loading') return

    if (!session) {
      router.push('/login')
      return
    }

    const role = (session.user as any).role
    if (role !== 'ADMIN' && role !== 'SUPER_ADMIN' && role !== 'CONTENT_MANAGER') {
      router.push('/dashboard/user')
      return
    }

    fetchAnalytics()
  }, [session, status, router])

  const fetchAnalytics = async () => {
    try {
      const res = await fetch('/api/analytics')
      if (res.ok) {
        const data = await res.json()
        setAnalytics(data)
      }
    } catch (error) {
      console.error('Error fetching analytics')
    } finally {
      setLoading(false)
    }
  }

  if (loading || status === 'loading') {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-lg text-gray-600">جاري التحميل...</div>
      </div>
    )
  }

  if (!analytics) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Navbar />
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="text-center py-12">
            <p className="text-gray-500">لا توجد بيانات تحليلية</p>
          </div>
        </div>
      </div>
    )
  }

  const statusData = [
    { name: 'معتمد', value: analytics.statusDistribution.approved },
    { name: 'مسودة', value: analytics.statusDistribution.draft },
    { name: 'قيد المراجعة', value: analytics.statusDistribution.underReview },
    { name: 'مرفوض', value: analytics.statusDistribution.rejected },
  ]

  const overviewCards = [
    { label: 'إجمالي البوسترات', value: analytics.overview.totalPosters, color: 'bg-blue-500' },
    { label: 'معتمد', value: analytics.overview.approvedPosters, color: 'bg-green-500' },
    { label: 'التحميلات', value: analytics.overview.totalDownloads, color: 'bg-purple-500' },
    { label: 'الطباعة', value: analytics.overview.totalPrints, color: 'bg-orange-500' },
    { label: 'مولد بالذكاء الاصطناعي', value: analytics.overview.aiGeneratedCount, color: 'bg-pink-500' },
    { label: 'المستخدمين', value: analytics.overview.totalUsers, color: 'bg-indigo-500' },
  ]

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">
            لوحة التحليلات
          </h1>
          <p className="text-gray-600">
            إحصائيات وأداء المنصة
          </p>
        </div>

        {/* Overview Cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
          {overviewCards.map((card, index) => (
            <div key={index} className="bg-white rounded-lg shadow-md p-6 border border-gray-200">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-600 mb-1">{card.label}</p>
                  <p className="text-2xl font-bold text-gray-900">{card.value.toLocaleString()}</p>
                </div>
                <div className={`${card.color} w-12 h-12 rounded-lg flex items-center justify-center`}>
                  <span className="text-white text-xl">📊</span>
                </div>
              </div>
            </div>
          ))}
        </div>

        {/* Charts */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
          {/* Status Distribution Pie Chart */}
          <div className="bg-white rounded-lg shadow-md p-6 border border-gray-200">
            <h2 className="text-xl font-bold text-gray-900 mb-4">توزيع البوسترات حسب الحالة</h2>
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={statusData}
                  cx="50%"
                  cy="50%"
                  labelLine={false}
                  label={({ name, percent }) => `${name} ${percent ? (percent * 100).toFixed(0) : 0}%`}
                  outerRadius={80}
                  fill="#8884d8"
                  dataKey="value"
                >
                  {statusData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </div>

          {/* Status Bar Chart */}
          <div className="bg-white rounded-lg shadow-md p-6 border border-gray-200">
            <h2 className="text-xl font-bold text-gray-900 mb-4">البوسترات حسب الحالة</h2>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={statusData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" />
                <YAxis />
                <Tooltip />
                <Bar dataKey="value" fill="#0ea5e9" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Additional Stats */}
        <div className="bg-white rounded-lg shadow-md p-6 border border-gray-200">
          <h2 className="text-xl font-bold text-gray-900 mb-4">إحصائيات إضافية</h2>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div>
              <p className="text-sm text-gray-600">القوالب</p>
              <p className="text-2xl font-bold text-gray-900">{analytics.overview.totalTemplates}</p>
            </div>
            <div>
              <p className="text-sm text-gray-600">البوسترات الأخيرة (30 يوم)</p>
              <p className="text-2xl font-bold text-gray-900">{analytics.overview.recentPosters}</p>
            </div>
            <div>
              <p className="text-sm text-gray-600">قيد المراجعة</p>
              <p className="text-2xl font-bold text-gray-900">{analytics.overview.underReviewPosters}</p>
            </div>
            <div>
              <p className="text-sm text-gray-600">مرفوض</p>
              <p className="text-2xl font-bold text-gray-900">{analytics.overview.rejectedPosters}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

