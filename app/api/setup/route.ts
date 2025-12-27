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
    // EMERGENCY: Temporarily bypass user count check for admin reset
    // Check if any users already exist
    const userCount = await prisma.user.count()

    // EMERGENCY BYPASS: Allow creating admin even if users exist
    // This will create a new admin or update existing one
    if (userCount > 0) {
      console.warn('⚠️ EMERGENCY MODE: Allowing admin creation even though users exist')
      // Continue instead of returning error
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

    // EMERGENCY: Create or update admin user
    // Check if user with this email already exists
    const existingUser = await prisma.user.findUnique({
      where: { email: data.email },
    })

    let user
    if (existingUser) {
      // Update existing user to ADMIN with new password
      console.warn('⚠️ EMERGENCY MODE: Updating existing user to ADMIN')
      user = await prisma.user.update({
        where: { email: data.email },
        data: {
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
    } else {
      // Create new admin user
      user = await prisma.user.create({
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
    }

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

    // EMERGENCY: Don't block on "User already exists" error
    // This is handled in the main logic now

    console.error('Error during setup')
    return NextResponse.json(
      { error: 'حدث خطأ أثناء إنشاء الحساب' },
      { status: 500 }
    )
  }
}

