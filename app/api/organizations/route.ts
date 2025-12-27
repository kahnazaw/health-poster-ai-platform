import { NextRequest, NextResponse } from 'next/server'
import { getServerSession } from 'next-auth'
import { authOptions } from '@/lib/auth'
import { prisma } from '@/lib/prisma'
import { z } from 'zod'
import { hasPermission } from '@/lib/permissions'

const createOrgSchema = z.object({
  name: z.string().min(1),
  code: z.string().min(1).max(20),
  description: z.string().optional(),
})

export async function GET(req: NextRequest) {
  try {
    const session = await getServerSession(authOptions)
    
    if (!session) {
      return NextResponse.json({ error: 'غير مصرح' }, { status: 401 })
    }

    const role = (session.user as any).role
    const userId = (session.user as any).id

    // Only SUPER_ADMIN can view all organizations
    if (!hasPermission(role, 'manageOrganization')) {
      // Users see only their organization
      const user = await prisma.user.findUnique({
        where: { id: userId },
        include: { organization: true },
      })

      if (user?.organization) {
        return NextResponse.json([user.organization])
      }
      return NextResponse.json([])
    }

    const organizations = await prisma.organization.findMany({
      include: {
        _count: {
          select: {
            users: true,
            templates: true,
          },
        },
      },
      orderBy: { createdAt: 'desc' },
    })

    return NextResponse.json(organizations)
  } catch (error) {
    console.error('Error fetching organizations')
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
    
    if (!hasPermission(role, 'manageOrganization')) {
      return NextResponse.json({ error: 'غير مصرح' }, { status: 403 })
    }

    const body = await req.json()
    const data = createOrgSchema.parse(body)

    // Check if code already exists
    const existing = await prisma.organization.findUnique({
      where: { code: data.code },
    })

    if (existing) {
      return NextResponse.json({ error: 'رمز المنظمة موجود بالفعل' }, { status: 400 })
    }

    const organization = await prisma.organization.create({
      data,
    })

    return NextResponse.json(organization, { status: 201 })
  } catch (error) {
    if (error instanceof z.ZodError) {
      return NextResponse.json({ error: 'بيانات غير صحيحة' }, { status: 400 })
    }
    console.error('Error creating organization')
    return NextResponse.json({ error: 'خطأ في الخادم' }, { status: 500 })
  }
}

