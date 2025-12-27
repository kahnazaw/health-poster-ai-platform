import type { Metadata } from 'next'
import { Cairo } from 'next/font/google'
import './globals.css'
import { Providers } from './providers'

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




