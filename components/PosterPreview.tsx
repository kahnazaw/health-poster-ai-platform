'use client'

import { forwardRef } from 'react'

interface PosterPreviewProps {
  title: string
  topic: string
  message: string
  footerText: string
}

const PosterPreview = forwardRef<HTMLDivElement, PosterPreviewProps>(
  ({ title, topic, message, footerText }, ref) => {
    const topicColors: Record<string, { bg: string; text: string; border: string }> = {
      'السكري': { bg: 'bg-red-50', text: 'text-red-700', border: 'border-red-300' },
      'ضغط الدم': { bg: 'bg-blue-50', text: 'text-blue-700', border: 'border-blue-300' },
      'التدخين': { bg: 'bg-gray-50', text: 'text-gray-700', border: 'border-gray-300' },
      'التطعيم': { bg: 'bg-green-50', text: 'text-green-700', border: 'border-green-300' },
      'الصحة النفسية': { bg: 'bg-purple-50', text: 'text-purple-700', border: 'border-purple-300' },
      'التغذية الصحية': { bg: 'bg-yellow-50', text: 'text-yellow-700', border: 'border-yellow-300' },
    }

    const colors = topicColors[topic] || {
      bg: 'bg-primary-50',
      text: 'text-primary-700',
      border: 'border-primary-300',
    }

    return (
      <div
        ref={ref}
        className="bg-white border-2 border-gray-200 rounded-lg p-8 mx-auto"
        style={{
          width: '210mm',
          minHeight: '297mm',
          maxWidth: '100%',
        }}
      >
        {/* Header */}
        <div className="text-center mb-8 pb-6 border-b-2 border-primary-200">
          {title && (
            <h1 className="text-4xl font-bold text-primary-700 mb-4">{title}</h1>
          )}
          {topic && (
            <span
              className={`inline-block px-4 py-2 rounded-full text-sm font-semibold ${colors.bg} ${colors.text} ${colors.border} border-2`}
            >
              {topic}
            </span>
          )}
        </div>

        {/* Main Content */}
        <div className="mb-8">
          {message && (
            <div className="bg-gray-50 border-r-4 border-primary-500 p-6 rounded-lg">
              <p className="text-xl text-gray-800 leading-relaxed whitespace-pre-line">
                {message}
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
            {footerText}
          </p>
          <p className="text-xs text-gray-500 text-center">
            دائرة صحة كركوك – قطاع كركوك الأول – وحدة تعزيز الصحة
          </p>
        </div>
      </div>
    )
  }
)

PosterPreview.displayName = 'PosterPreview'

export default PosterPreview

