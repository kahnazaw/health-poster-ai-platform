'use client'

import { useEffect, useState } from 'react'
import { useSession } from 'next-auth/react'
import { useRouter } from 'next/navigation'
import Navbar from '@/components/Navbar'
import { motion } from 'framer-motion'
import { TrendingUp, Users, FileText, Download, Activity, Sparkles, BarChart3 } from 'lucide-react'
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
  AreaChart,
  Area,
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
            <motion.div
              key={index}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: index * 0.1 }}
              whileHover={{ scale: 1.02, y: -5 }}
              className="bg-white/80 backdrop-blur-sm rounded-xl shadow-lg p-6 border border-gray-200/50 hover:shadow-xl transition-all duration-300"
            >
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-600 mb-1 font-medium">{card.label}</p>
                  <p className="text-3xl font-bold bg-gradient-to-r from-primary-600 to-sky-600 bg-clip-text text-transparent">
                    {card.value.toLocaleString()}
                  </p>
                </div>
                <motion.div
                  whileHover={{ rotate: 360 }}
                  transition={{ duration: 0.5 }}
                  className={`${card.color} w-14 h-14 rounded-xl flex items-center justify-center shadow-lg`}
                >
                  {index === 0 && <FileText className="w-7 h-7 text-white" />}
                  {index === 1 && <TrendingUp className="w-7 h-7 text-white" />}
                  {index === 2 && <Download className="w-7 h-7 text-white" />}
                  {index === 3 && <Download className="w-7 h-7 text-white" />}
                  {index === 4 && <Sparkles className="w-7 h-7 text-white" />}
                  {index === 5 && <Users className="w-7 h-7 text-white" />}
                </motion.div>
              </div>
            </motion.div>
          ))}
        </div>

        {/* Charts */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
          {/* Status Distribution Pie Chart */}
          <motion.div
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            transition={{ delay: 0.2 }}
            className="bg-white/80 backdrop-blur-sm rounded-xl shadow-lg p-6 border border-gray-200/50"
          >
            <h2 className="text-xl font-bold text-gray-900 mb-4 flex items-center space-x-2 space-x-reverse">
              <Activity className="w-5 h-5 text-primary-600" />
              <span>توزيع البوسترات حسب الحالة</span>
            </h2>
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
          </motion.div>

          {/* Status Bar Chart */}
          <motion.div
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            transition={{ delay: 0.3 }}
            className="bg-white/80 backdrop-blur-sm rounded-xl shadow-lg p-6 border border-gray-200/50"
          >
            <h2 className="text-xl font-bold text-gray-900 mb-4 flex items-center space-x-2 space-x-reverse">
              <BarChart3 className="w-5 h-5 text-sky-600" />
              <span>البوسترات حسب الحالة</span>
            </h2>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={statusData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
                <XAxis dataKey="name" stroke="#6b7280" />
                <YAxis stroke="#6b7280" />
                <Tooltip
                  contentStyle={{
                    backgroundColor: 'white',
                    border: '1px solid #e5e7eb',
                    borderRadius: '8px',
                  }}
                />
                <Bar dataKey="value" fill="url(#colorGradient)" radius={[8, 8, 0, 0]} />
                <defs>
                  <linearGradient id="colorGradient" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="0%" stopColor="#06b6d4" />
                    <stop offset="100%" stopColor="#0ea5e9" />
                  </linearGradient>
                </defs>
              </BarChart>
            </ResponsiveContainer>
          </motion.div>
        </div>

        {/* Additional Stats */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.4 }}
          className="bg-white/80 backdrop-blur-sm rounded-xl shadow-lg p-6 border border-gray-200/50"
        >
          <h2 className="text-xl font-bold text-gray-900 mb-6 flex items-center space-x-2 space-x-reverse">
            <TrendingUp className="w-5 h-5 text-primary-600" />
            <span>إحصائيات إضافية</span>
          </h2>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
            {[
              { label: 'القوالب', value: analytics.overview.totalTemplates, icon: FileText },
              { label: 'البوسترات الأخيرة (30 يوم)', value: analytics.overview.recentPosters, icon: Activity },
              { label: 'قيد المراجعة', value: analytics.overview.underReviewPosters, icon: Activity },
              { label: 'مرفوض', value: analytics.overview.rejectedPosters, icon: Activity },
            ].map((stat, index) => (
              <motion.div
                key={index}
                whileHover={{ scale: 1.05 }}
                className="text-center p-4 bg-gradient-to-br from-gray-50 to-white rounded-lg border border-gray-200"
              >
                <stat.icon className="w-6 h-6 text-primary-600 mx-auto mb-2" />
                <p className="text-sm text-gray-600 mb-1">{stat.label}</p>
                <p className="text-2xl font-bold text-gray-900">{stat.value.toLocaleString()}</p>
              </motion.div>
            ))}
          </div>
        </motion.div>
      </div>
    </div>
  )
}

