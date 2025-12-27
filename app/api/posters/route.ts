import { NextRequest, NextResponse } from 'next/server'
import { getServerSession } from 'next-auth'
import { authOptions } from '@/lib/auth'
import { prisma } from '@/lib/prisma'

export async function GET(req: NextRequest) {
  try {
    const session = await getServerSession(authOptions)
    
    if (!session) {
      return NextResponse.json({ error: 'غير مصرح' }, { status: 401 })
    }

    const isAdmin = (session.user as any).role === 'ADMIN'
    
    const posters = await prisma.poster.findMany({
      where: isAdmin ? {} : { userId: (session.user as any).id },
      include: {
        user: {
          select: {
            name: true,
            email: true,
          },
        },
      },
      orderBy: { createdAt: 'desc' },
    })

    return NextResponse.json(posters)
  } catch (error) {
    return NextResponse.json({ error: 'خطأ في الخادم' }, { status: 500 })
  }
}




