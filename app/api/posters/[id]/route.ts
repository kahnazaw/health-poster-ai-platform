import { NextRequest, NextResponse } from 'next/server'
import { getServerSession } from 'next-auth'
import { authOptions } from '@/lib/auth'
import { prisma } from '@/lib/prisma'

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
      include: {
        user: {
          select: {
            name: true,
            email: true,
          },
        },
      },
    })

    if (!poster) {
      return NextResponse.json({ error: 'البوستر غير موجود' }, { status: 404 })
    }

    const isAdmin = (session.user as any).role === 'ADMIN'
    const isOwner = poster.userId === (session.user as any).id

    if (!isAdmin && !isOwner) {
      return NextResponse.json({ error: 'غير مصرح' }, { status: 403 })
    }

    return NextResponse.json(poster)
  } catch (error) {
    console.error('Error fetching poster')
    return NextResponse.json({ error: 'خطأ في الخادم' }, { status: 500 })
  }
}

export async function DELETE(
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

    const isAdmin = (session.user as any).role === 'ADMIN'
    const isOwner = poster.userId === (session.user as any).id

    if (!isAdmin && !isOwner) {
      return NextResponse.json({ error: 'غير مصرح' }, { status: 403 })
    }

    await prisma.poster.delete({
      where: { id: params.id },
    })

    return NextResponse.json({ message: 'تم حذف البوستر بنجاح' })
  } catch (error) {
    console.error('Error deleting poster')
    return NextResponse.json({ error: 'خطأ في الخادم' }, { status: 500 })
  }
}




