'use client'

import { useEffect, useState } from 'react'
import { useSession } from 'next-auth/react'
import { useRouter } from 'next/navigation'
import { motion } from 'framer-motion'
import { Users, FileText, TrendingUp, Activity, Sparkles, BarChart3 } from 'lucide-react'
import Navbar from '@/components/Navbar'
import Card from '@/components/ui/Card'
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts'

interface Stats {
  totalUsers: number
  totalPosters: number
  activeUsersThisWeek: number
  mostActiveUnits: { name: string; count: number }[]
  postersByStatus: { status: string; count: number }[]
}

export default function AdminDashboard() {
  const { data: session, status } = useSession()
  const router = useRouter()
  const [loading, setLoading] = useState(true)
  const [stats, setStats] = useState<Stats | null>(null)

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

    fetchStats()
  }, [session, status, router])

  const fetchStats = async () => {
    try {
      const [analyticsRes, usersRes] = await Promise.all([
        fetch('/api/analytics'),
        fetch('/api/users'),
      ])

      if (analyticsRes.ok && usersRes.ok) {
        const analytics = await analyticsRes.json()
        const users = await usersRes.json()
        
        // Calculate active users this week
        const weekAgo = new Date()
        weekAgo.setDate(weekAgo.getDate() - 7)
        const activeUsersThisWeek = users.filter((user: any) => {
          const userDate = new Date(user.createdAt)
          return userDate >= weekAgo
        }).length

        // Mock most active units (you can replace with real data)
        const mostActiveUnits = [
          { name: 'وحدة تعزيز الصحة', count: 45 },
          { name: 'قسم التوعية', count: 32 },
          { name: 'قسم الإعلام', count: 28 },
        ]

        setStats({
          totalUsers: analytics.totalUsers || users.length,
          totalPosters: analytics.totalPosters || 0,
          activeUsersThisWeek,
          mostActiveUnits,
          postersByStatus: analytics.postersByStatus || [],
        })
      }
    } catch (error) {
      console.error('Error fetching stats:', error)
    } finally {
      setLoading(false)
    }
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

  const COLORS = ['#06b6d4', '#0ea5e9', '#22c55e', '#f59e0b', '#ef4444']

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
            لوحة التحكم الإدارية
          </h1>
          <p className="text-gray-600 text-lg">
            نظرة شاملة على أداء المنصة
          </p>
        </motion.div>

        {/* Stats Overview */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          <Card delay={0.1}>
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 mb-1">إجمالي البوسترات</p>
                <p className="text-3xl font-bold text-primary-600">{stats?.totalPosters || 0}</p>
              </div>
              <div className="p-3 bg-primary-100 rounded-lg">
                <FileText className="w-6 h-6 text-primary-600" />
              </div>
            </div>
          </Card>

          <Card delay={0.2}>
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 mb-1">المستخدمين النشطين</p>
                <p className="text-3xl font-bold text-sky-600">{stats?.activeUsersThisWeek || 0}</p>
                <p className="text-xs text-gray-500 mt-1">هذا الأسبوع</p>
              </div>
              <div className="p-3 bg-sky-100 rounded-lg">
                <Users className="w-6 h-6 text-sky-600" />
              </div>
            </div>
          </Card>

          <Card delay={0.3}>
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 mb-1">إجمالي المستخدمين</p>
                <p className="text-3xl font-bold text-green-600">{stats?.totalUsers || 0}</p>
              </div>
              <div className="p-3 bg-green-100 rounded-lg">
                <Activity className="w-6 h-6 text-green-600" />
              </div>
            </div>
          </Card>

          <Card delay={0.4}>
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 mb-1">النمو</p>
                <p className="text-3xl font-bold text-purple-600">+12%</p>
                <p className="text-xs text-gray-500 mt-1">الشهر الماضي</p>
              </div>
              <div className="p-3 bg-purple-100 rounded-lg">
                <TrendingUp className="w-6 h-6 text-purple-600" />
              </div>
            </div>
          </Card>
        </div>

        {/* Charts */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
          <Card delay={0.5}>
            <h3 className="text-xl font-bold text-gray-900 mb-4 flex items-center gap-2">
              <BarChart3 className="w-5 h-5 text-primary-600" />
              البوسترات حسب الحالة
            </h3>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={stats?.postersByStatus || []}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="status" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Bar dataKey="count" fill="#06b6d4" />
              </BarChart>
            </ResponsiveContainer>
          </Card>

          <Card delay={0.6}>
            <h3 className="text-xl font-bold text-gray-900 mb-4 flex items-center gap-2">
              <Sparkles className="w-5 h-5 text-sky-600" />
              الوحدات الأكثر نشاطاً
            </h3>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={stats?.mostActiveUnits || []} layout="vertical">
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis type="number" />
                <YAxis dataKey="name" type="category" width={120} />
                <Tooltip />
                <Bar dataKey="count" fill="#0ea5e9" />
              </BarChart>
            </ResponsiveContainer>
          </Card>
        </div>

        {/* Quick Actions */}
        <Card delay={0.7}>
          <h3 className="text-xl font-bold text-gray-900 mb-4">إجراءات سريعة</h3>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <motion.a
              href="/dashboard/admin/logs"
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
              className="p-4 bg-gradient-to-r from-primary-50 to-sky-50 rounded-lg border border-primary-200 hover:border-primary-400 transition-colors"
            >
              <Activity className="w-6 h-6 text-primary-600 mb-2" />
              <h4 className="font-semibold text-gray-900">سجل الأنشطة</h4>
              <p className="text-sm text-gray-600">عرض جميع الأنشطة والأحداث</p>
            </motion.a>

            <motion.a
              href="/dashboard/admin/analytics"
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
              className="p-4 bg-gradient-to-r from-sky-50 to-primary-50 rounded-lg border border-sky-200 hover:border-sky-400 transition-colors"
            >
              <TrendingUp className="w-6 h-6 text-sky-600 mb-2" />
              <h4 className="font-semibold text-gray-900">التحليلات المتقدمة</h4>
              <p className="text-sm text-gray-600">رسوم بيانية وتقارير مفصلة</p>
            </motion.a>

            <motion.a
              href="/dashboard/admin/organizations"
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
              className="p-4 bg-gradient-to-r from-green-50 to-emerald-50 rounded-lg border border-green-200 hover:border-green-400 transition-colors"
            >
              <Users className="w-6 h-6 text-green-600 mb-2" />
              <h4 className="font-semibold text-gray-900">إدارة المنظمات</h4>
              <p className="text-sm text-gray-600">إدارة المستخدمين والمنظمات</p>
            </motion.a>
          </div>
        </Card>
      </div>
    </div>
  )
}
