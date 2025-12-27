import { NextRequest, NextResponse } from 'next/server'
import { getServerSession } from 'next-auth'
import { authOptions } from '@/lib/auth'
import { prisma } from '@/lib/prisma'
import { canEditPoster, hasPermission } from '@/lib/permissions'

/**
 * Track export/download
 */
export async function POST(
  req: NextRequest,
  { params }: { params: { id: string } }
) {
  try {
    const session = await getServerSession(authOptions)
    
    if (!session) {
      return NextResponse.json({ error: 'غير مصرح' }, { status: 401 })
    }

    const body = await req.json()
    const { type } = body // 'download' or 'print'

    const poster = await prisma.poster.findUnique({
      where: { id: params.id },
    })

    if (!poster) {
      return NextResponse.json({ error: 'البوستر غير موجود' }, { status: 404 })
    }

    const role = (session.user as any).role
    const userId = (session.user as any).id

    // Check if user can export (approved or admin)
    if (poster.status !== 'APPROVED' && !hasPermission(role, 'editAllContent')) {
      if (!canEditPoster(role, poster.userId, userId)) {
        return NextResponse.json({ 
          error: 'يمكن تصدير البوسترات المعتمدة فقط' 
        }, { status: 403 })
      }
    }

    // Update download/print count
    await prisma.poster.update({
      where: { id: params.id },
      data: type === 'download' 
        ? { downloadCount: { increment: 1 } }
        : { printCount: { increment: 1 } },
    })

    return NextResponse.json({ success: true })
  } catch (error) {
    console.error('Error tracking export')
    return NextResponse.json({ error: 'خطأ في الخادم' }, { status: 500 })
  }
}

