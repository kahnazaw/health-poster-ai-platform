import { NextRequest, NextResponse } from 'next/server'
import { getServerSession } from 'next-auth'
import { authOptions } from '@/lib/auth'
import { prisma } from '@/lib/prisma'
import { z } from 'zod'
import { hasPermission } from '@/lib/permissions'

const createTemplateSchema = z.object({
  title: z.string().min(1),
  description: z.string().optional(),
  topic: z.string().min(1),
  category: z.string().min(1),
  targetAudience: z.string().optional(),
  content: z.string().min(1),
  isPublic: z.boolean().optional().default(false),
})

export async function GET(req: NextRequest) {
  try {
    const session = await getServerSession(authOptions)
    
    if (!session) {
      return NextResponse.json({ error: 'غير مصرح' }, { status: 401 })
    }

    const role = (session.user as any).role
    const userId = (session.user as any).id

    // Get templates based on permissions
    const canViewAll = hasPermission(role, 'viewAnalytics')
    
    const templates = await prisma.template.findMany({
      where: {
        OR: [
          { isPublic: true },
          { createdById: userId },
          ...(canViewAll ? [{}] : []),
        ],
        isActive: true,
      },
      include: {
        createdBy: {
          select: {
            name: true,
            email: true,
          },
        },
      },
      orderBy: { createdAt: 'desc' },
    })

    return NextResponse.json(templates)
  } catch (error) {
    console.error('Error fetching templates')
    return NextResponse.json({ error: 'خطأ في الخادم' }, { status: 500 })
  }
}

export async function POST(req: NextRequest) {
  try {
    const session = await getServerSession(authOptions)
    
    if (!session) {
      return NextResponse.json({ error: 'غير مصرح' }, { status: 401 })
    }

    const role = (session.user as any).role
    
    if (!hasPermission(role, 'createTemplate')) {
      return NextResponse.json({ error: 'غير مصرح' }, { status: 403 })
    }

    const body = await req.json()
    const data = createTemplateSchema.parse(body)

    const template = await prisma.template.create({
      data: {
        ...data,
        createdById: (session.user as any).id,
      },
      include: {
        createdBy: {
          select: {
            name: true,
            email: true,
          },
        },
      },
    })

    return NextResponse.json(template, { status: 201 })
  } catch (error) {
    if (error instanceof z.ZodError) {
      return NextResponse.json({ error: 'بيانات غير صحيحة' }, { status: 400 })
    }
    console.error('Error creating template')
    return NextResponse.json({ error: 'خطأ في الخادم' }, { status: 500 })
  }
}

