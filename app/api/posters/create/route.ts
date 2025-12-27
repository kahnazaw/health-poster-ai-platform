import { NextRequest, NextResponse } from 'next/server'
import { getServerSession } from 'next-auth'
import { authOptions } from '@/lib/auth'
import { prisma } from '@/lib/prisma'
import { z } from 'zod'

const createPosterSchema = z.object({
  title: z.string().min(1, 'العنوان مطلوب'),
  topic: z.string().min(1, 'الموضوع مطلوب'),
  message: z.string().min(1, 'الرسالة مطلوبة'),
  footerText: z.string().optional(),
})

export async function POST(req: NextRequest) {
  try {
    const session = await getServerSession(authOptions)

    if (!session) {
      return NextResponse.json({ error: 'غير مصرح' }, { status: 401 })
    }

    const body = await req.json()
    const data = createPosterSchema.parse(body)

    const posterData = {
      title: data.title,
      topic: data.topic,
      message: data.message,
      footerText: data.footerText || 'وزارة الصحة',
    }

    const poster = await prisma.poster.create({
      data: {
        title: data.title,
        topic: data.topic,
        content: JSON.stringify(posterData),
        userId: (session.user as any).id,
      },
    })

    return NextResponse.json({
      id: poster.id,
      ...posterData,
    })
  } catch (error) {
    if (error instanceof z.ZodError) {
      return NextResponse.json({ error: error.errors[0].message }, { status: 400 })
    }
    console.error('Error creating poster')
    return NextResponse.json({ error: 'خطأ في الخادم' }, { status: 500 })
  }
}

