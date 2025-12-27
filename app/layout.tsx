import type { Metadata } from 'next'
import { Cairo } from 'next/font/google'
import './globals.css'
import { Providers } from './providers'
import { ensureAdmin } from '@/scripts/ensure-admin'

// Ensure admin user exists on startup (production only)
if (process.env.NODE_ENV === 'production') {
  ensureAdmin().catch(() => {
    // Silently handle errors to prevent startup crashes
  })
}

const cairo = Cairo({ 
  subsets: ['arabic', 'latin'],
  weight: ['400', '600', '700'],
  variable: '--font-cairo',
})

export const metadata: Metadata = {
  title: 'منصة بوسترات التوعية الصحية',
  description: 'منصة مؤسسية رسمية لتوليد بوسترات التوعية الصحية بالذكاء الاصطناعي',
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="ar" dir="rtl">
      <body className={`${cairo.variable} font-arabic`}>
        <Providers>{children}</Providers>
      </body>
    </html>
  )
}




