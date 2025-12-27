'use client'

import { useEffect, useState } from 'react'
import { useSession } from 'next-auth/react'
import { useRouter } from 'next/navigation'
import Link from 'next/link'
import Navbar from '@/components/Navbar'
import PosterGenerator from '@/components/PosterGenerator'

interface Poster {
  id: string
  title: string
  topic: string
  createdAt: string
}

export default function UserDashboard() {
  const { data: session, status } = useSession()
  const router = useRouter()
  const [loading, setLoading] = useState(true)
  const [posters, setPosters] = useState<Poster[]>([])
  const [showGenerator, setShowGenerator] = useState(false)
  const [loadingPosters, setLoadingPosters] = useState(true)

  useEffect(() => {
    if (status === 'loading') return

    if (!session) {
      router.push('/login')
      return
    }

    setLoading(false)
    fetchPosters()
  }, [session, status, router])

  const fetchPosters = async () => {
    try {
      setLoadingPosters(true)
      const res = await fetch('/api/posters')
      if (res.ok) {
        const data = await res.json()
        setPosters(data)
      }
    } catch (error) {
      console.error('Error fetching posters:', error)
    } finally {
      setLoadingPosters(false)
    }
  }

  if (loading || status === 'loading') {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-lg text-gray-600">جاري التحميل...</div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-8 flex justify-between items-center">
          <div>
            <h1 className="text-3xl font-bold text-gray-900 mb-2">
              لوحة تحكم المستخدم
            </h1>
            <p className="text-gray-600">
              إدارة بوسترات التوعية الصحية
            </p>
          </div>
          <button
            onClick={() => setShowGenerator(!showGenerator)}
            className="bg-primary-600 hover:bg-primary-700 text-white px-6 py-3 rounded-lg font-medium transition"
          >
            {showGenerator ? 'إخفاء النموذج' : 'إنشاء بوستر جديد'}
          </button>
        </div>

        {showGenerator && (
          <div className="mb-8">
            <PosterGenerator onPosterCreated={fetchPosters} />
          </div>
        )}

        <div className="bg-white rounded-lg shadow-md p-6">
          <h2 className="text-xl font-bold text-gray-900 mb-4">البوسترات السابقة</h2>
          {loadingPosters ? (
            <div className="text-center py-8 text-gray-600">جاري التحميل...</div>
          ) : posters.length === 0 ? (
            <div className="text-center py-8 text-gray-500">
              <p>لا توجد بوسترات حتى الآن</p>
              <p className="text-sm mt-2">انقر على "إنشاء بوستر جديد" لبدء إنشاء أول بوستر</p>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                      العنوان
                    </th>
                    <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                      الموضوع
                    </th>
                    <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                      تاريخ الإنشاء
                    </th>
                    <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                      الإجراءات
                    </th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {posters.map((poster) => (
                    <tr key={poster.id} className="hover:bg-gray-50">
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                        {poster.title}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {poster.topic}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {new Date(poster.createdAt).toLocaleDateString('ar-EG', {
                          year: 'numeric',
                          month: 'long',
                          day: 'numeric',
                        })}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                        <Link
                          href={`/posters/${poster.id}`}
                          className="text-primary-600 hover:text-primary-900"
                        >
                          عرض
                        </Link>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}




