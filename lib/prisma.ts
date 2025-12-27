import { PrismaClient } from '@prisma/client'

const globalForPrisma = globalThis as unknown as {
  prisma: PrismaClient | undefined
}

// Get DATABASE_URL - don't validate during build time
const databaseUrl = process.env.DATABASE_URL

// Lazy validation: Only check at runtime, not during build
function validateDatabaseUrl() {
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
  if (isServerRuntime && databaseUrl) {
    const dbInfo = databaseUrl.replace(/:[^:@]+@/, ':****@') // Hide password
    console.log('🔌 Database connection:', dbInfo.includes('postgres') ? 'PostgreSQL ✅' : 'UNKNOWN ⚠️')
  }
}

// Create Prisma client - use placeholder during build if DATABASE_URL is not available
export const prisma =
  globalForPrisma.prisma ??
  new PrismaClient({
    log: process.env.NODE_ENV === 'development' ? ['error', 'warn'] : ['error'],
    datasources: {
      db: {
        // Use actual URL if available, otherwise placeholder (will be validated at runtime)
        url: databaseUrl || 'postgresql://placeholder:placeholder@localhost:5432/placeholder',
      },
    },
  })

// Validate DATABASE_URL on first actual database operation (lazy validation)
// This ensures build succeeds even without DATABASE_URL
if (typeof window === 'undefined') {
  // Store original methods
  const originalUserFindUnique = prisma.user.findUnique
  const originalUserCount = prisma.user.count
  const originalUserCreate = prisma.user.create
  const originalUserUpdate = prisma.user.update
  const originalConnect = prisma.$connect

  // Wrap key methods to validate on first use
  prisma.user.findUnique = function(...args: any[]) {
    validateDatabaseUrl()
    return originalUserFindUnique.apply(this, args)
  }

  prisma.user.count = function(...args: any[]) {
    validateDatabaseUrl()
    return originalUserCount.apply(this, args)
  }

  prisma.user.create = function(...args: any[]) {
    validateDatabaseUrl()
    return originalUserCreate.apply(this, args)
  }

  prisma.user.update = function(...args: any[]) {
    validateDatabaseUrl()
    return originalUserUpdate.apply(this, args)
  }

  prisma.$connect = function(...args: any[]) {
    validateDatabaseUrl()
    return originalConnect.apply(this, args)
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
