import { PrismaClient } from '@prisma/client'

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

// Create Prisma client - use placeholder during build if DATABASE_URL is not available
// Validation will happen on first actual database operation via $connect()
// Connection pooling: url = pooled connection, directUrl = direct connection (for migrations)
export const prisma =
  globalForPrisma.prisma ??
  new PrismaClient({
    log: process.env.NODE_ENV === 'development' ? ['error', 'warn'] : ['error'],
    datasources: {
      db: {
        // Use actual URL if available, otherwise placeholder (will be validated at runtime)
        url: databaseUrl || 'postgresql://placeholder:placeholder@localhost:5432/placeholder',
        // directUrl is used for migrations when connection pooling is enabled
        // If not set, Prisma will use url for both operations
        ...(directUrl && { directUrl }),
      },
    },
  })

// Override $connect to validate DATABASE_URL on first connection attempt
const originalConnect = prisma.$connect.bind(prisma)
prisma.$connect = async function() {
  validateDatabaseUrl()
  return originalConnect()
}

if (process.env.NODE_ENV !== 'production') {
  globalForPrisma.prisma = prisma
} else {
  // In production, ensure we don't create multiple instances
  if (!globalForPrisma.prisma) {
    globalForPrisma.prisma = prisma
  }
}
