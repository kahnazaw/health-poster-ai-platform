import { NextResponse } from 'next/server'
import { prisma } from '@/lib/prisma'

/**
 * Check if setup is allowed (no users exist)
 * EMERGENCY: Temporarily always return canSetup: true for admin reset
 */
export async function GET() {
  try {
    const userCount = await prisma.user.count()

    // EMERGENCY BYPASS: Always allow setup for admin reset
    return NextResponse.json({
      canSetup: true, // FORCED TO TRUE FOR EMERGENCY RESET
      userCount,
      emergencyMode: true, // Flag to indicate emergency mode
    })
  } catch (error) {
    console.error('Error checking setup status')
    // Even on error, allow setup in emergency mode
    return NextResponse.json(
      { error: 'خطأ في الخادم', canSetup: true, emergencyMode: true },
      { status: 500 }
    )
  }
}

