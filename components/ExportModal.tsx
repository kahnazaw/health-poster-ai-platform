'use client'

import { useState, useRef } from 'react'
import { generateQRCode, exportPoster, ExportFormat } from '@/lib/export'

interface ExportModalProps {
  isOpen: boolean
  onClose: () => void
  posterId: string
  posterTitle: string
  posterElement: HTMLElement | null
  posterStatus: string
  userRole: string
}

export default function ExportModal({
  isOpen,
  onClose,
  posterId,
  posterTitle,
  posterElement,
  posterStatus,
  userRole,
}: ExportModalProps) {
  const [selectedFormat, setSelectedFormat] = useState<ExportFormat>('png-hq')
  const [loading, setLoading] = useState(false)
  const [progress, setProgress] = useState(0)
  const [qrCode, setQrCode] = useState<string | null>(null)
  const [publicUrl, setPublicUrl] = useState<string | null>(null)
  const qrCanvasRef = useRef<HTMLCanvasElement>(null)

  // Check if export is allowed
  const canExport = posterStatus === 'APPROVED' || 
    userRole === 'ADMIN' || 
    userRole === 'SUPER_ADMIN' ||
    userRole === 'CONTENT_MANAGER'

  const handleExport = async () => {
    if (!posterElement) {
      alert('عنصر البوستر غير متاح')
      return
    }

    if (!canExport) {
      alert('يمكن تصدير البوسترات المعتمدة فقط')
      return
    }

    setLoading(true)
    setProgress(0)

    try {
      await exportPoster({
        format: selectedFormat,
        element: posterElement,
        filename: posterTitle,
        onProgress: setProgress,
      })

      // Track export
      await fetch(`/api/posters/${posterId}/export`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ type: 'download' }),
      })

      setLoading(false)
      setProgress(0)
    } catch (error) {
      alert('حدث خطأ أثناء التصدير')
      setLoading(false)
      setProgress(0)
    }
  }

  const handleGenerateQR = async () => {
    try {
      setLoading(true)
      const res = await fetch(`/api/posters/${posterId}/qr`)
      
      if (res.ok) {
        const data = await res.json()
        setQrCode(data.qrCode)
        setPublicUrl(data.publicUrl)
      } else {
        alert('حدث خطأ أثناء توليد QR code')
      }
    } catch (error) {
      alert('حدث خطأ أثناء توليد QR code')
    } finally {
      setLoading(false)
    }
  }

  if (!isOpen) return null

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto">
        <div className="p-6">
          <div className="flex justify-between items-center mb-6">
            <h2 className="text-2xl font-bold text-gray-900">تصدير البوستر</h2>
            <button
              onClick={onClose}
              className="text-gray-400 hover:text-gray-600 text-2xl"
            >
              ×
            </button>
          </div>

          {!canExport && (
            <div className="bg-yellow-50 border border-yellow-200 text-yellow-800 px-4 py-3 rounded-lg mb-4">
              ⚠️ يمكن تصدير البوسترات المعتمدة فقط. حالياً: {posterStatus}
            </div>
          )}

          <div className="space-y-6">
            {/* Format Selection */}
            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-3">
                اختر صيغة التصدير
              </label>
              <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
                <button
                  onClick={() => setSelectedFormat('png-hq')}
                  className={`p-3 border-2 rounded-lg text-sm font-medium transition ${
                    selectedFormat === 'png-hq'
                      ? 'border-primary-500 bg-primary-50 text-primary-700'
                      : 'border-gray-300 hover:border-gray-400'
                  }`}
                >
                  PNG عالي الجودة
                </button>
                <button
                  onClick={() => setSelectedFormat('pdf-a4')}
                  className={`p-3 border-2 rounded-lg text-sm font-medium transition ${
                    selectedFormat === 'pdf-a4'
                      ? 'border-primary-500 bg-primary-50 text-primary-700'
                      : 'border-gray-300 hover:border-gray-400'
                  }`}
                >
                  PDF A4
                </button>
                <button
                  onClick={() => setSelectedFormat('pdf-a3')}
                  className={`p-3 border-2 rounded-lg text-sm font-medium transition ${
                    selectedFormat === 'pdf-a3'
                      ? 'border-primary-500 bg-primary-50 text-primary-700'
                      : 'border-gray-300 hover:border-gray-400'
                  }`}
                >
                  PDF A3
                </button>
                <button
                  onClick={() => setSelectedFormat('instagram')}
                  className={`p-3 border-2 rounded-lg text-sm font-medium transition ${
                    selectedFormat === 'instagram'
                      ? 'border-primary-500 bg-primary-50 text-primary-700'
                      : 'border-gray-300 hover:border-gray-400'
                  }`}
                >
                  Instagram
                </button>
                <button
                  onClick={() => setSelectedFormat('whatsapp')}
                  className={`p-3 border-2 rounded-lg text-sm font-medium transition ${
                    selectedFormat === 'whatsapp'
                      ? 'border-primary-500 bg-primary-50 text-primary-700'
                      : 'border-gray-300 hover:border-gray-400'
                  }`}
                >
                  WhatsApp
                </button>
                <button
                  onClick={() => setSelectedFormat('facebook')}
                  className={`p-3 border-2 rounded-lg text-sm font-medium transition ${
                    selectedFormat === 'facebook'
                      ? 'border-primary-500 bg-primary-50 text-primary-700'
                      : 'border-gray-300 hover:border-gray-400'
                  }`}
                >
                  Facebook
                </button>
              </div>
            </div>

            {/* QR Code Section */}
            <div className="border-t border-gray-200 pt-4">
              <h3 className="text-lg font-semibold text-gray-900 mb-3">
                QR Code للمشاركة
              </h3>
              {qrCode ? (
                <div className="space-y-3">
                  <img src={qrCode} alt="QR Code" className="mx-auto w-48 h-48" />
                  {publicUrl && (
                    <div className="text-center">
                      <p className="text-sm text-gray-600 mb-2">رابط المشاركة:</p>
                      <a
                        href={publicUrl}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="text-primary-600 hover:underline text-sm break-all"
                      >
                        {publicUrl}
                      </a>
                    </div>
                  )}
                </div>
              ) : (
                <button
                  onClick={handleGenerateQR}
                  disabled={loading}
                  className="w-full bg-gray-100 hover:bg-gray-200 text-gray-700 font-medium py-2 px-4 rounded-lg transition disabled:opacity-50"
                >
                  توليد QR Code
                </button>
              )}
            </div>

            {/* Progress */}
            {loading && progress > 0 && (
              <div>
                <div className="flex justify-between text-sm text-gray-600 mb-1">
                  <span>جاري التصدير...</span>
                  <span>{progress}%</span>
                </div>
                <div className="w-full bg-gray-200 rounded-full h-2">
                  <div
                    className="bg-primary-600 h-2 rounded-full transition-all duration-300"
                    style={{ width: `${progress}%` }}
                  />
                </div>
              </div>
            )}

            {/* Actions */}
            <div className="flex space-x-3 space-x-reverse justify-end pt-4 border-t border-gray-200">
              <button
                onClick={onClose}
                className="px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition"
              >
                إلغاء
              </button>
              <button
                onClick={handleExport}
                disabled={loading || !canExport}
                className="px-6 py-2 bg-primary-600 hover:bg-primary-700 text-white rounded-lg font-medium transition disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {loading ? 'جاري التصدير...' : 'تصدير'}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

