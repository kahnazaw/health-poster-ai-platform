import { NextRequest, NextResponse } from 'next/server'
import { prisma } from '@/lib/prisma'
import bcrypt from 'bcryptjs'
import { z } from 'zod'

const setupSchema = z.object({
  email: z.string().email('البريد الإلكتروني غير صحيح'),
  password: z.string().min(8, 'كلمة المرور يجب أن تكون 8 أحرف على الأقل'),
  name: z.string().min(1, 'الاسم مطلوب'),
})

/**
 * One-time initial setup to create the first ADMIN user
 * This endpoint can only be used ONCE - when no users exist
 */
export async function POST(req: NextRequest) {
  try {
    // Check if any users already exist
    const userCount = await prisma.user.count()

    if (userCount > 0) {
      return NextResponse.json(
        { error: 'تم إعداد النظام بالفعل. لا يمكن إنشاء حساب مدير آخر.' },
        { status: 403 }
      )
    }

    // Validate input
    const body = await req.json()
    const data = setupSchema.parse(body)

    // Additional password validation
    const password = data.password
    if (!/[A-Z]/.test(password)) {
      return NextResponse.json(
        { error: 'كلمة المرور يجب أن تحتوي على حرف كبير واحد على الأقل' },
        { status: 400 }
      )
    }
    if (!/[a-z]/.test(password)) {
      return NextResponse.json(
        { error: 'كلمة المرور يجب أن تحتوي على حرف صغير واحد على الأقل' },
        { status: 400 }
      )
    }
    if (!/[0-9]/.test(password)) {
      return NextResponse.json(
        { error: 'كلمة المرور يجب أن تحتوي على رقم واحد على الأقل' },
        { status: 400 }
      )
    }
    if (!/[!@#$%^&*(),.?":{}|<>]/.test(password)) {
      return NextResponse.json(
        { error: 'كلمة المرور يجب أن تحتوي على رمز خاص واحد على الأقل' },
        { status: 400 }
      )
    }

    // Hash password securely
    const hashedPassword = await bcrypt.hash(password, 12)

    // Create admin user in a transaction to ensure atomicity
    const user = await prisma.$transaction(async (tx) => {
      // Double-check no users exist (race condition protection)
      const existingCount = await tx.user.count()
      if (existingCount > 0) {
        throw new Error('User already exists')
      }

      // Create admin user
      return await tx.user.create({
        data: {
          email: data.email,
          password: hashedPassword,
          name: data.name,
          role: 'ADMIN',
        },
        select: {
          id: true,
          email: true,
          name: true,
          role: true,
          createdAt: true,
        },
      })
    })

    // Optionally create default organization
    try {
      await prisma.organization.create({
        data: {
          name: 'دائرة صحة كركوك',
          code: 'KIRKUK1',
          description: 'المنظمة الافتراضية',
        },
      })
    } catch (orgError) {
      // Ignore organization creation errors (non-critical)
      console.log('Could not create default organization')
    }

    return NextResponse.json(
      {
        message: 'تم إنشاء حساب المدير بنجاح',
        user: {
          id: user.id,
          email: user.email,
          name: user.name,
        },
      },
      { status: 201 }
    )
  } catch (error) {
    if (error instanceof z.ZodError) {
      return NextResponse.json(
        { error: error.errors[0].message },
        { status: 400 }
      )
    }

    if (error instanceof Error && error.message === 'User already exists') {
      return NextResponse.json(
        { error: 'تم إعداد النظام بالفعل' },
        { status: 403 }
      )
    }

    console.error('Error during setup')
    return NextResponse.json(
      { error: 'حدث خطأ أثناء إنشاء الحساب' },
      { status: 500 }
    )
  }
}

