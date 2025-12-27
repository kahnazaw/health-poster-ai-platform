'use client'

import { useEffect, useState, useRef } from 'react'
import { useParams } from 'next/navigation'

interface Poster {
  id: string
  title: string
  topic: string
  content: string
  status: string
}

export default function PublicPosterViewPage() {
  const params = useParams()
  const [poster, setPoster] = useState<Poster | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const posterRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    fetchPoster()
  }, [params.publicId])

  const fetchPoster = async () => {
    try {
      setLoading(true)
      const res = await fetch(`/api/posters/public/${params.publicId}`)
      
      if (res.ok) {
        const data = await res.json()
        setPoster(data)
      } else {
        const errorData = await res.json()
        setError(errorData.error || 'البوستر غير موجود')
      }
    } catch (err) {
      setError('حدث خطأ أثناء تحميل البوستر')
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-lg text-gray-600">جاري التحميل...</div>
      </div>
    )
  }

  if (error || !poster) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <h1 className="text-2xl font-bold text-gray-900 mb-2">البوستر غير موجود</h1>
          <p className="text-gray-600">{error || 'البوستر غير متاح للعرض العام'}</p>
        </div>
      </div>
    )
  }

  let content: any = {}
  try {
    content = JSON.parse(poster.content)
  } catch {
    content = { title: poster.title, points: [], closing: '' }
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="bg-white rounded-lg shadow-lg p-6">
          <div
            ref={posterRef}
            className="bg-white border-2 border-gray-200 rounded-lg p-8 mx-auto"
            style={{
              width: '210mm',
              minHeight: '297mm',
              maxWidth: '100%',
            }}
          >
            {/* Header */}
            <div className="text-center mb-8 pb-6 border-b-2 border-primary-200">
              <h1 className="text-4xl font-bold text-primary-700 mb-2">
                {content.title || poster.title}
              </h1>
            </div>

            {/* Content */}
            <div className="space-y-6 mb-8">
              {content.points && content.points.length > 0 ? (
                <ul className="space-y-4 list-none">
                  {content.points.map((point: string, index: number) => (
                    <li key={index} className="flex items-start space-x-3 space-x-reverse">
                      <span className="flex-shrink-0 w-8 h-8 bg-primary-100 text-primary-700 rounded-full flex items-center justify-center font-bold mt-1">
                        {index + 1}
                      </span>
                      <p className="text-lg text-gray-800 leading-relaxed flex-1">
                        {point}
                      </p>
                    </li>
                  ))}
                </ul>
              ) : (
                <div className="text-lg text-gray-800 leading-relaxed">
                  {content.message || 'لا يوجد محتوى'}
                </div>
              )}

              {content.closing && (
                <div className="bg-medical-50 border-r-4 border-medical-500 p-4 rounded-lg mt-8">
                  <p className="text-lg text-gray-800 font-medium">
                    {content.closing}
                  </p>
                </div>
              )}
            </div>

            {/* Footer */}
            <div className="mt-auto pt-8 border-t-2 border-gray-200 space-y-3">
              <p className="text-sm text-gray-600 text-center italic">
                هذه المادة للتوعية الصحية العامة ولا تغني عن استشارة الطبيب
              </p>
              <p className="text-sm text-gray-700 text-center font-semibold">
                دائرة صحة كركوك – قطاع كركوك الأول – وحدة تعزيز الصحة
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

