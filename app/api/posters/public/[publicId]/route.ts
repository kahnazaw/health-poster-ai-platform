import { NextRequest, NextResponse } from 'next/server'
import { prisma } from '@/lib/prisma'

/**
 * Public poster view (no authentication required)
 */
export async function GET(
  req: NextRequest,
  { params }: { params: { publicId: string } }
) {
  try {
    const poster = await prisma.poster.findUnique({
      where: { publicId: params.publicId },
      select: {
        id: true,
        title: true,
        topic: true,
        content: true,
        status: true,
      },
    })

    if (!poster) {
      return NextResponse.json({ error: 'البوستر غير موجود' }, { status: 404 })
    }

    // Only approved posters can be viewed publicly
    if (poster.status !== 'APPROVED') {
      return NextResponse.json({ error: 'البوستر غير متاح للعرض العام' }, { status: 403 })
    }

    return NextResponse.json(poster)
  } catch (error) {
    console.error('Error fetching public poster')
    return NextResponse.json({ error: 'خطأ في الخادم' }, { status: 500 })
  }
}

