import { NextRequest, NextResponse } from 'next/server'
import { getServerSession } from 'next-auth'
import { authOptions } from '@/lib/auth'
import { prisma } from '@/lib/prisma'
import { z } from 'zod'
import { canReview, canApprove } from '@/lib/permissions'

const reviewSchema = z.object({
  status: z.enum(['APPROVED', 'REJECTED', 'REQUEST_CHANGES']),
  comment: z.string().min(1),
})

export async function POST(
  req: NextRequest,
  { params }: { params: { id: string } }
) {
  try {
    const session = await getServerSession(authOptions)
    
    if (!session) {
      return NextResponse.json({ error: 'غير مصرح' }, { status: 401 })
    }

    const role = (session.user as any).role
    const userId = (session.user as any).id

    if (!canReview(role)) {
      return NextResponse.json({ error: 'غير مصرح' }, { status: 403 })
    }

    const body = await req.json()
    const { status, comment } = reviewSchema.parse(body)

    // Check if can approve (only for APPROVED status)
    if (status === 'APPROVED' && !canApprove(role)) {
      return NextResponse.json({ error: 'غير مصرح للموافقة' }, { status: 403 })
    }

    const poster = await prisma.poster.findUnique({
      where: { id: params.id },
    })

    if (!poster) {
      return NextResponse.json({ error: 'البوستر غير موجود' }, { status: 404 })
    }

    // Create review
    const review = await prisma.review.create({
      data: {
        posterId: params.id,
        reviewerId: userId,
        comment,
        status,
      },
    })

    // Update poster status
    const updateData: any = {
      reviewerId: userId,
    }

    if (status === 'APPROVED') {
      updateData.status = 'APPROVED'
      updateData.approvedAt = new Date()
    } else if (status === 'REJECTED') {
      updateData.status = 'REJECTED'
      updateData.rejectedAt = new Date()
      updateData.rejectionReason = comment
    } else {
      updateData.status = 'UNDER_REVIEW'
    }

    await prisma.poster.update({
      where: { id: params.id },
      data: updateData,
    })

    // Create status history
    await prisma.statusHistory.create({
      data: {
        posterId: params.id,
        status: updateData.status,
        changedBy: userId,
        comment,
      },
    })

    return NextResponse.json({ review, message: 'تمت المراجعة بنجاح' })
  } catch (error) {
    if (error instanceof z.ZodError) {
      return NextResponse.json({ error: 'بيانات غير صحيحة' }, { status: 400 })
    }
    console.error('Error reviewing poster')
    return NextResponse.json({ error: 'خطأ في الخادم' }, { status: 500 })
  }
}

