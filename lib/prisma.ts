import { PrismaClient } from '@prisma/client'
import { Pool } from '@prisma/adapter-pg'
import { Pool as PgPool } from 'pg'

const globalForPrisma = globalThis as unknown as {
  prisma: PrismaClient | undefined
}

// Get DATABASE_URL and DIRECT_URL - don't validate during build time
const databaseUrl = process.env.DATABASE_URL
const directUrl = process.env.DIRECT_URL // For connection pooling (migrations use this)
const skipValidation = process.env.SKIP_ENV_VALIDATION === '1'

// Lazy validation: Only check at runtime, not during build
function validateDatabaseUrl() {
  // Skip validation if SKIP_ENV_VALIDATION is set (during build)
  if (skipValidation) {
    return
  }

  // Only validate in production runtime (not during build)
  // Check if we're in a server context (not browser) and in production
  const isServerRuntime = typeof window === 'undefined'
  const isProduction = process.env.NODE_ENV === 'production'
  
  if (isServerRuntime && isProduction && !databaseUrl) {
    console.error('❌ CRITICAL: DATABASE_URL is not set at runtime!')
    throw new Error('DATABASE_URL environment variable is required')
  }

  // Verify it's PostgreSQL, not SQLite (only at runtime)
  if (isServerRuntime && isProduction && databaseUrl && (databaseUrl.startsWith('file:') || databaseUrl.includes('dev.db'))) {
    console.error('❌ CRITICAL: DATABASE_URL points to SQLite file! Expected PostgreSQL connection string.')
    throw new Error('DATABASE_URL must be a PostgreSQL connection string, not a file path')
  }

  // Log database connection info (only at runtime, without exposing password)
  if (isServerRuntime && databaseUrl && !databaseUrl.includes('placeholder')) {
    const dbInfo = databaseUrl.replace(/:[^:@]+@/, ':****@') // Hide password
    console.log('🔌 Database connection:', dbInfo.includes('postgres') ? 'PostgreSQL ✅' : 'UNKNOWN ⚠️')
  }
}

// Create Prisma client - use adapter for Prisma 7.2.0
// Validation will happen on first actual database operation via $connect()
// Connection pooling: url = pooled connection, directUrl = direct connection (for migrations)

// CRITICAL: Ensure we're using DATABASE_URL from process.env, not a hardcoded value
const actualDatabaseUrl = process.env.DATABASE_URL
console.log('🔍 Prisma Client Initialization:')
console.log('  - DATABASE_URL from process.env:', actualDatabaseUrl ? 'SET' : 'NOT SET')
if (actualDatabaseUrl) {
  // Log connection info without exposing password
  const dbInfo = actualDatabaseUrl.replace(/:[^:@]+@/, ':****@')
  console.log('  - Connection string:', dbInfo.substring(0, 50) + '...')
  console.log('  - Is PostgreSQL:', dbInfo.includes('postgres'))
}

let adapter: Pool | undefined
if (actualDatabaseUrl && !actualDatabaseUrl.includes('placeholder')) {
  try {
    // Use DATABASE_URL directly from process.env - no fallback
    const pgPool = new PgPool({ connectionString: actualDatabaseUrl })
    adapter = new Pool(pgPool)
    console.log('✅ Prisma adapter initialized with DATABASE_URL from process.env')
  } catch (adapterError) {
    console.error('❌ Failed to create Prisma adapter:', adapterError)
    // Continue without adapter - Prisma will use connection string directly
  }
} else {
  console.warn('⚠️ DATABASE_URL not set or contains placeholder - adapter not initialized')
  console.warn('⚠️ Prisma will need DATABASE_URL at runtime')
  if (!actualDatabaseUrl) {
    console.error('❌ CRITICAL: DATABASE_URL is not set in process.env!')
  }
}

export const prisma =
  globalForPrisma.prisma ??
  new PrismaClient({
    adapter: adapter,
    log: process.env.NODE_ENV === 'development' ? ['error', 'warn', 'query'] : ['error'],
  })

// Override $connect to validate DATABASE_URL on first connection attempt
// Add error handling to prevent app crash on slow connections
const originalConnect = prisma.$connect.bind(prisma)
prisma.$connect = async function() {
  try {
    validateDatabaseUrl()
    // Add timeout for connection attempts (30 seconds)
    const connectPromise = originalConnect()
    const timeoutPromise = new Promise((_, reject) => 
      setTimeout(() => reject(new Error('Database connection timeout after 30s')), 30000)
    )
    return await Promise.race([connectPromise, timeoutPromise])
  } catch (error: any) {
    // Log error but don't crash the app - allow retry on next request
    console.error('⚠️ Database connection error:', error.message)
    console.error('⚠️ App will continue, but database operations may fail')
    // Re-throw to let the caller handle it
    throw error
  }
}

if (process.env.NODE_ENV !== 'production') {
  globalForPrisma.prisma = prisma
} else {
  // In production, ensure we don't create multiple instances
  if (!globalForPrisma.prisma) {
    globalForPrisma.prisma = prisma
  }
}
