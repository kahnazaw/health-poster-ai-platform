import { NextResponse } from 'next/server'
import { prisma } from '@/lib/prisma'

/**
 * Check if setup is allowed (no users exist)
 */
export async function GET() {
  try {
    const userCount = await prisma.user.count()

    return NextResponse.json({
      canSetup: userCount === 0,
      userCount,
    })
  } catch (error) {
    console.error('Error checking setup status')
    return NextResponse.json(
      { error: 'خطأ في الخادم', canSetup: false },
      { status: 500 }
    )
  }
}

