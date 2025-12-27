'use client'

import { useEffect, useState, useRef } from 'react'
import { useSession } from 'next-auth/react'
import { useRouter, useParams } from 'next/navigation'
import { useReactToPrint } from 'react-to-print'
import Navbar from '@/components/Navbar'
import ExportModal from '@/components/ExportModal'

interface Poster {
  id: string
  title: string
  topic: string
  content: string
  status: string
  createdAt: string
}

export default function PosterViewPage() {
  const { data: session, status } = useSession()
  const router = useRouter()
  const params = useParams()
  const [poster, setPoster] = useState<Poster | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [showExportModal, setShowExportModal] = useState(false)
  const posterRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    if (status === 'loading') return

    if (!session) {
      router.push('/login')
      return
    }

    fetchPoster()
  }, [session, status, router, params.id])

  const fetchPoster = async () => {
    try {
      setLoading(true)
      const res = await fetch(`/api/posters/${params.id}`)
      
      if (res.ok) {
        const data = await res.json()
        setPoster(data)
      } else {
        const errorData = await res.json()
        setError(errorData.error || 'حدث خطأ أثناء تحميل البوستر')
      }
    } catch (err) {
      setError('حدث خطأ أثناء الاتصال بالخادم')
    } finally {
      setLoading(false)
    }
  }

  const handlePrint = useReactToPrint({
    content: () => posterRef.current,
    documentTitle: poster?.title || 'بوستر توعية صحية',
  })

  const handleDownloadImage = async () => {
    if (!posterRef.current) return

    try {
      const html2canvas = (await import('html2canvas')).default
      const canvas = await html2canvas(posterRef.current, {
        scale: 2,
        useCORS: true,
        backgroundColor: '#ffffff',
        logging: false,
      })

      const link = document.createElement('a')
      link.download = `${poster?.title || 'poster'}.png`
      link.href = canvas.toDataURL('image/png')
      link.click()
    } catch (err) {
      alert('حدث خطأ أثناء تحميل الصورة')
    }
  }

  const handleDownloadPDF = async () => {
    if (!posterRef.current) return

    try {
      const html2canvas = (await import('html2canvas')).default
      const { default: jsPDF } = await import('jspdf')
      
      const canvas = await html2canvas(posterRef.current, {
        scale: 2,
        useCORS: true,
        backgroundColor: '#ffffff',
        logging: false,
      })

      const imgData = canvas.toDataURL('image/png')
      const pdf = new jsPDF({
        orientation: 'portrait',
        unit: 'mm',
        format: 'a4',
      })

      const pdfWidth = pdf.internal.pageSize.getWidth()
      const pdfHeight = pdf.internal.pageSize.getHeight()
      const imgWidth = canvas.width
      const imgHeight = canvas.height
      const ratio = Math.min(pdfWidth / imgWidth, pdfHeight / imgHeight)
      const imgX = (pdfWidth - imgWidth * ratio) / 2
      const imgY = 0

      pdf.addImage(imgData, 'PNG', imgX, imgY, imgWidth * ratio, imgHeight * ratio)
      pdf.save(`${poster?.title || 'poster'}.pdf`)
    } catch (err) {
      alert('حدث خطأ أثناء تحميل PDF')
    }
  }

  if (loading || status === 'loading') {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-lg text-gray-600">جاري التحميل...</div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Navbar />
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
            {error}
          </div>
        </div>
      </div>
    )
  }

  if (!poster) {
    return null
  }

  let content: any = {}
  try {
    content = JSON.parse(poster.content)
  } catch {
    content = { title: poster.title, points: [], closing: '' }
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-6 flex justify-between items-center">
          <div>
            <h1 className="text-3xl font-bold text-gray-900 mb-2">
              {poster.title}
            </h1>
            <p className="text-gray-600">
              الموضوع: {poster.topic}
            </p>
          </div>
          <div className="flex gap-3 flex-wrap">
            <button
              onClick={() => setShowExportModal(true)}
              className="px-4 py-2 bg-primary-600 hover:bg-primary-700 text-white rounded-lg font-medium transition"
            >
              تصدير متعدد القنوات
            </button>
            <button
              onClick={handleDownloadImage}
              className="px-4 py-2 bg-medical-600 hover:bg-medical-700 text-white rounded-lg font-medium transition"
            >
              تحميل صورة
            </button>
            <button
              onClick={handleDownloadPDF}
              className="px-4 py-2 bg-medical-600 hover:bg-medical-700 text-white rounded-lg font-medium transition"
            >
              تحميل PDF
            </button>
            <button
              onClick={handlePrint}
              className="px-4 py-2 bg-gray-600 hover:bg-gray-700 text-white rounded-lg font-medium transition"
            >
              طباعة
            </button>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-md p-6">
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

      {/* Export Modal */}
      {poster && (
        <ExportModal
          isOpen={showExportModal}
          onClose={() => setShowExportModal(false)}
          posterId={poster.id}
          posterTitle={poster.title}
          posterElement={posterRef.current}
          posterStatus={poster.status}
          userRole={(session?.user as any)?.role || 'USER'}
        />
      )}
    </div>
  )
}

