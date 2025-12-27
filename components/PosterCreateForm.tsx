'use client'

import { useState, useRef } from 'react'
import { useSession } from 'next-auth/react'
import PosterPreview from './PosterPreview'

const HEALTH_TOPICS = [
  'السكري',
  'ضغط الدم',
  'التدخين',
  'التطعيم',
  'الصحة النفسية',
  'التغذية الصحية',
]

interface PosterData {
  title: string
  topic: string
  message: string
  footerText?: string
}

export default function PosterCreateForm() {
  const { data: session } = useSession()
  const [formData, setFormData] = useState<PosterData>({
    title: '',
    topic: '',
    message: '',
    footerText: 'وزارة الصحة',
  })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [posterId, setPosterId] = useState<string | null>(null)
  const previewRef = useRef<HTMLDivElement>(null)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)

    if (!formData.title.trim() || !formData.topic || !formData.message.trim()) {
      setError('الرجاء ملء جميع الحقول المطلوبة')
      setLoading(false)
      return
    }

    try {
      const res = await fetch('/api/posters/create', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData),
      })

      if (res.ok) {
        const data = await res.json()
        setPosterId(data.id)
      } else {
        const errorData = await res.json()
        setError(errorData.error || 'حدث خطأ أثناء إنشاء البوستر')
      }
    } catch (err) {
      setError('حدث خطأ أثناء الاتصال بالخادم')
    } finally {
      setLoading(false)
    }
  }

  const handleDownloadPNG = async () => {
    if (!previewRef.current) return

    try {
      const html2canvas = (await import('html2canvas')).default
      const canvas = await html2canvas(previewRef.current, {
        scale: 2,
        useCORS: true,
        backgroundColor: '#ffffff',
        logging: false,
      })

      const link = document.createElement('a')
      link.download = `${formData.title || 'poster'}.png`
      link.href = canvas.toDataURL('image/png')
      link.click()
    } catch (err) {
      alert('حدث خطأ أثناء تحميل الصورة')
    }
  }

  const handleDownloadPDF = async () => {
    if (!previewRef.current) return

    try {
      const html2canvas = (await import('html2canvas')).default
      const { default: jsPDF } = await import('jspdf')

      const canvas = await html2canvas(previewRef.current, {
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
      pdf.save(`${formData.title || 'poster'}.pdf`)
    } catch (err) {
      alert('حدث خطأ أثناء تحميل PDF')
    }
  }

  return (
    <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
      {/* Form Section */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label htmlFor="title" className="block text-sm font-medium text-gray-700 mb-2">
              عنوان البوستر *
            </label>
            <input
              id="title"
              type="text"
              value={formData.title}
              onChange={(e) => setFormData({ ...formData, title: e.target.value })}
              required
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent outline-none"
              placeholder="أدخل عنوان البوستر"
            />
          </div>

          <div>
            <label htmlFor="topic" className="block text-sm font-medium text-gray-700 mb-2">
              موضوع التوعية الصحية *
            </label>
            <select
              id="topic"
              value={formData.topic}
              onChange={(e) => setFormData({ ...formData, topic: e.target.value })}
              required
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent outline-none"
            >
              <option value="">اختر الموضوع</option>
              {HEALTH_TOPICS.map((topic) => (
                <option key={topic} value={topic}>
                  {topic}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label htmlFor="message" className="block text-sm font-medium text-gray-700 mb-2">
              الرسالة الرئيسية *
            </label>
            <textarea
              id="message"
              value={formData.message}
              onChange={(e) => setFormData({ ...formData, message: e.target.value })}
              required
              rows={6}
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent outline-none resize-none"
              placeholder="أدخل الرسالة الرئيسية للبوستر"
            />
          </div>

          <div>
            <label htmlFor="footerText" className="block text-sm font-medium text-gray-700 mb-2">
              نص التذييل (اختياري)
            </label>
            <input
              id="footerText"
              type="text"
              value={formData.footerText}
              onChange={(e) => setFormData({ ...formData, footerText: e.target.value })}
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent outline-none"
              placeholder="وزارة الصحة"
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

        <div className="mt-6 pt-6 border-t border-gray-200 space-y-3">
          <button
            onClick={handleDownloadPNG}
            disabled={!formData.title || !formData.message}
            className="w-full bg-medical-600 hover:bg-medical-700 text-white font-semibold py-3 px-4 rounded-lg transition disabled:opacity-50 disabled:cursor-not-allowed"
          >
            تحميل صورة PNG
          </button>
          <button
            onClick={handleDownloadPDF}
            disabled={!formData.title || !formData.message}
            className="w-full bg-medical-600 hover:bg-medical-700 text-white font-semibold py-3 px-4 rounded-lg transition disabled:opacity-50 disabled:cursor-not-allowed"
          >
            تحميل PDF
          </button>
        </div>
      </div>

      {/* Preview Section */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <h2 className="text-xl font-bold text-gray-900 mb-4">معاينة البوستر</h2>
        <div className="flex justify-center overflow-auto">
          <PosterPreview
            ref={previewRef}
            title={formData.title}
            topic={formData.topic}
            message={formData.message}
            footerText={formData.footerText || 'وزارة الصحة'}
          />
        </div>
      </div>
    </div>
  )
}

