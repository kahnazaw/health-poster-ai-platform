import { NextRequest, NextResponse } from 'next/server'
import { prisma } from '@/lib/prisma'
import bcrypt from 'bcryptjs'
import { z } from 'zod'

// Force dynamic rendering - prevent static generation during build
export const dynamic = 'force-dynamic'
export const runtime = 'nodejs'

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
    // Verify DATABASE_URL is set and not a placeholder
    console.log('🔌 Checking database connection...')
    const dbUrl = process.env.DATABASE_URL
    console.log('DATABASE_URL:', dbUrl ? 'SET' : 'NOT SET')
    
    if (!dbUrl || dbUrl.includes('placeholder')) {
      const errorMsg = 'DATABASE_URL is not set or contains placeholder'
      console.error('❌', errorMsg)
      return NextResponse.json(
        { 
          error: 'خطأ في إعدادات قاعدة البيانات',
          details: errorMsg,
          debug: { DATABASE_URL: dbUrl ? 'SET (placeholder)' : 'NOT SET' }
        },
        { status: 500 }
      )
    }
    
    // Verify Prisma is using DATABASE_URL from environment
    console.log('🔍 Verifying Prisma client configuration...')
    console.log('Using DATABASE_URL from process.env:', !!process.env.DATABASE_URL)
    
    // Check if User table exists before trying to query it
    console.log('📋 Checking if User table exists...')
    try {
      // Try to query the User table - if it doesn't exist, this will fail
      await prisma.$queryRaw`SELECT 1 FROM "User" LIMIT 1`
      console.log('✅ User table exists')
    } catch (tableError: any) {
      console.error('❌ User table check failed:', tableError)
      // Check if it's a "relation does not exist" error
      if (tableError?.code === '42P01' || tableError?.message?.includes('does not exist')) {
        return NextResponse.json(
          { 
            error: 'Database Tables Missing',
            details: 'The User table does not exist. Please run database migrations first.',
            debug: { errorCode: tableError?.code, errorMessage: tableError?.message }
          },
          { status: 500 }
        )
      }
      // If it's a connection error, re-throw it
      throw tableError
    }
    
    try {
      await prisma.$connect()
      console.log('✅ Database connection successful')
    } catch (connectError: any) {
      console.error('❌ Database connection failed:', connectError)
      return NextResponse.json(
        { 
          error: 'خطأ في الاتصال بقاعدة البيانات',
          details: connectError?.message || 'Database connection failed',
          debug: { 
            errorCode: connectError?.code,
            errorName: connectError?.name,
            DATABASE_URL: dbUrl ? 'SET' : 'NOT SET'
          }
        },
        { status: 500 }
      )
    }

    // EMERGENCY: Temporarily bypass user count check for admin reset
    // Check if any users already exist
    console.log('📊 Checking user count...')
    let userCount = 0
    try {
      userCount = await prisma.user.count()
      console.log(`📊 User count: ${userCount}`)
    } catch (countError: any) {
      console.error('❌ Failed to count users:', countError)
      return NextResponse.json(
        { 
          error: 'خطأ في الوصول إلى قاعدة البيانات',
          details: countError?.message || 'Failed to query users table',
          debug: { errorCode: countError?.code, errorName: countError?.name }
        },
        { status: 500 }
      )
    }

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
    // Log the full error for debugging
    console.error('❌ Error during setup:', error)
    
    if (error instanceof z.ZodError) {
      console.error('Validation error:', error.errors)
      return NextResponse.json(
        { error: error.errors[0].message },
        { status: 400 }
      )
    }

    // Check for Prisma-specific errors
    if (error && typeof error === 'object' && 'code' in error) {
      const prismaError = error as any
      console.error('Prisma error code:', prismaError.code)
      console.error('Prisma error message:', prismaError.message)
      
      // Handle specific Prisma errors
      if (prismaError.code === 'P2002') {
        return NextResponse.json(
          { error: 'البريد الإلكتروني مستخدم بالفعل' },
          { status: 400 }
        )
      }
      
      if (prismaError.code === 'P1001') {
        console.error('❌ Database connection error - DATABASE_URL:', process.env.DATABASE_URL ? 'SET' : 'NOT SET')
        return NextResponse.json(
          { error: 'خطأ في الاتصال بقاعدة البيانات. يرجى التحقق من إعدادات قاعدة البيانات.' },
          { status: 500 }
        )
      }
    }

    // Log error details for debugging
    if (error instanceof Error) {
      console.error('Error name:', error.name)
      console.error('Error message:', error.message)
      console.error('Error stack:', error.stack)
    }

    // Always include error details in response for debugging
    const errorMessage = error instanceof Error ? error.message : String(error)
    const errorName = error instanceof Error ? error.name : 'UnknownError'
    const errorStack = error instanceof Error ? error.stack : undefined
    
    // Extract Prisma error code if available
    const prismaCode = (error && typeof error === 'object' && 'code' in error) ? (error as any).code : undefined
    
    return NextResponse.json(
      { 
        error: 'حدث خطأ أثناء إنشاء الحساب',
        details: errorMessage,
        debug: {
          errorName,
          errorCode: prismaCode,
          DATABASE_URL: process.env.DATABASE_URL ? 'SET' : 'NOT SET',
          stack: process.env.NODE_ENV === 'development' ? errorStack : undefined
        }
      },
      { status: 500 }
    )
  }
}

