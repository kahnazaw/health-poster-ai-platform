'use client'

import { useState, useRef } from 'react'
import { useReactToPrint } from 'react-to-print'

interface PosterContent {
  title: string
  points: string[]
  closing: string
}

export default function PosterGenerator() {
  const [topic, setTopic] = useState('')
  const [loading, setLoading] = useState(false)
  const [content, setContent] = useState<PosterContent | null>(null)
  const [error, setError] = useState('')
  const posterRef = useRef<HTMLDivElement>(null)

  const handleGenerate = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!topic.trim()) {
      setError('الرجاء إدخال موضوع التوعية الصحية')
      return
    }

    setLoading(true)
    setError('')
    setContent(null)

    try {
      const res = await fetch('/api/posters/generate', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ topic: topic.trim() }),
      })

      if (res.ok) {
        const data = await res.json()
        setContent(data.content)
      } else {
        const errorData = await res.json()
        setError(errorData.error || 'حدث خطأ أثناء توليد البوستر')
      }
    } catch (err) {
      setError('حدث خطأ أثناء الاتصال بالخادم')
    } finally {
      setLoading(false)
    }
  }

  const handlePrint = useReactToPrint({
    content: () => posterRef.current,
    documentTitle: content?.title || 'بوستر توعية صحية',
  })

  const handleDownloadImage = async () => {
    if (!posterRef.current) return

    try {
      const html2canvas = (await import('html2canvas')).default
      const canvas = await html2canvas(posterRef.current, {
        scale: 2,
        useCORS: true,
        backgroundColor: '#ffffff',
      })

      const link = document.createElement('a')
      link.download = `${content?.title || 'poster'}.png`
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
      pdf.save(`${content?.title || 'poster'}.pdf`)
    } catch (err) {
      alert('حدث خطأ أثناء تحميل PDF')
    }
  }

  return (
    <div className="space-y-6">
      <div className="bg-white rounded-lg shadow-md p-6">
        <form onSubmit={handleGenerate} className="space-y-4">
          <div>
            <label htmlFor="topic" className="block text-sm font-medium text-gray-700 mb-2">
              موضوع التوعية الصحية
            </label>
            <input
              id="topic"
              type="text"
              value={topic}
              onChange={(e) => setTopic(e.target.value)}
              placeholder="مثال: نظافة الأسنان، التغذية الصحية، الرياضة..."
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent outline-none"
            />
          </div>

          {error && (
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg text-sm">
              {error}
            </div>
          )}

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-primary-600 hover:bg-primary-700 text-white font-semibold py-3 px-4 rounded-lg transition duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {loading ? 'جاري التوليد...' : 'توليد البوستر'}
          </button>
        </form>
      </div>

      {content && (
        <div className="bg-white rounded-lg shadow-md p-6">
          <div className="mb-4 flex flex-wrap gap-3">
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
              className="px-4 py-2 bg-primary-600 hover:bg-primary-700 text-white rounded-lg font-medium transition"
            >
              طباعة
            </button>
          </div>

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
                {content.title}
              </h1>
            </div>

            {/* Content */}
            <div className="space-y-6 mb-8">
              <ul className="space-y-4 list-none">
                {content.points.map((point, index) => (
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

              <div className="bg-medical-50 border-r-4 border-medical-500 p-4 rounded-lg mt-8">
                <p className="text-lg text-gray-800 font-medium">
                  {content.closing}
                </p>
              </div>
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
      )}
    </div>
  )
}

