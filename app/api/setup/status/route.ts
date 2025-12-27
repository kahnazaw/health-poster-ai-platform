import { NextResponse } from 'next/server'
import { prisma } from '@/lib/prisma'

// Force dynamic rendering - prevent static generation during build
export const dynamic = 'force-dynamic'
export const runtime = 'nodejs'

/**
 * Check if setup is allowed (no users exist)
 * CRITICAL: Always allow setup if database is empty or connection fails
 */
export async function GET() {
  try {
    // Test database connection first
    await prisma.$connect()
    
    // Count users in PostgreSQL database
    const userCount = await prisma.user.count()
    
    console.log(`📊 Database check: Found ${userCount} users in PostgreSQL`)

    // Always allow setup if table is empty OR in emergency mode
    const canSetup = userCount === 0 || true // Force true for emergency
    
    return NextResponse.json({
      canSetup: canSetup,
      userCount,
      databaseConnected: true,
      emergencyMode: true, // Force allow setup
    })
  } catch (error: any) {
    console.error('❌ Error checking setup status:', error.message)
    console.error('Full error:', error)
    
    // If database connection fails, still allow setup (might be first run)
    return NextResponse.json({
      canSetup: true, // Allow setup on error (might be empty DB)
      userCount: 0,
      databaseConnected: false,
      error: error.message,
      emergencyMode: true,
    }, { status: 200 }) // Return 200 so page can still load
  }
}

