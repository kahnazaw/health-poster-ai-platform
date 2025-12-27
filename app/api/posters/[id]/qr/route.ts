import { NextRequest, NextResponse } from 'next/server'
import { getServerSession } from 'next-auth'
import { authOptions } from '@/lib/auth'
import { prisma } from '@/lib/prisma'
import QRCode from 'qrcode'

/**
 * Generate or get QR code for poster
 */
export async function GET(
  req: NextRequest,
  { params }: { params: { id: string } }
) {
  try {
    const session = await getServerSession(authOptions)
    
    if (!session) {
      return NextResponse.json({ error: 'غير مصرح' }, { status: 401 })
    }

    const poster = await prisma.poster.findUnique({
      where: { id: params.id },
    })

    if (!poster) {
      return NextResponse.json({ error: 'البوستر غير موجود' }, { status: 404 })
    }

    // Generate public ID if not exists
    let publicId = poster.publicId
    if (!publicId) {
      // Generate a short, unique public ID
      publicId = `poster-${poster.id.slice(0, 8)}-${Date.now().toString(36)}`
      await prisma.poster.update({
        where: { id: params.id },
        data: { publicId },
      })
    }

    // Generate QR code URL
    const baseUrl = process.env.NEXTAUTH_URL || req.nextUrl.origin
    const publicUrl = `${baseUrl}/public/posters/${publicId}`

    // Generate QR code
    const qrDataUrl = await QRCode.toDataURL(publicUrl, {
      width: 300,
      margin: 2,
      color: {
        dark: '#000000',
        light: '#FFFFFF',
      },
    })

    // Store QR code URL if not exists
    if (!poster.qrCodeUrl) {
      await prisma.poster.update({
        where: { id: params.id },
        data: { qrCodeUrl: qrDataUrl },
      })
    }

    return NextResponse.json({
      qrCode: qrDataUrl,
      publicUrl,
      publicId,
    })
  } catch (error) {
    console.error('Error generating QR code')
    return NextResponse.json({ error: 'خطأ في الخادم' }, { status: 500 })
  }
}

