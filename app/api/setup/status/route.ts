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
    // Test database connection first with timeout
    const connectPromise = prisma.$connect()
    const timeoutPromise = new Promise((_, reject) => 
      setTimeout(() => reject(new Error('Database connection timeout')), 5000)
    )
    
    await Promise.race([connectPromise, timeoutPromise])
    
    // CRITICAL: Actually query the PostgreSQL database to count users
    // Use findMany with take: 0 to check if table exists and is empty
    const userCount = await prisma.user.count()
    
    console.log(`📊 Database check: Found ${userCount} users in PostgreSQL`)

    // CRITICAL: If table is empty (userCount === 0), MUST return canSetup: true
    // This is the truth - if table is empty, setup is allowed
    const canSetup = userCount === 0
    
    return NextResponse.json({
      canSetup: canSetup,
      userCount,
      databaseConnected: true,
      message: userCount === 0 ? 'Database is empty - setup allowed' : `Found ${userCount} users - setup ${canSetup ? 'allowed' : 'not allowed'}`,
    })
  } catch (error: any) {
    console.error('❌ Error checking setup status:', error.message)
    console.error('Full error:', error)
    
    // If database connection fails or times out, allow setup (might be first run or empty DB)
    // This is safe - if we can't connect, we can't know if users exist
    return NextResponse.json({
      canSetup: true, // Allow setup on error (might be empty DB or connection issue)
      userCount: 0,
      databaseConnected: false,
      error: error.message,
      message: 'Database connection failed - allowing setup as fallback',
    }, { status: 200 }) // Return 200 so page can still load
  }
}

