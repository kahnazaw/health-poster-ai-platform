import { PrismaClient } from '@prisma/client'

const globalForPrisma = globalThis as unknown as {
  prisma: PrismaClient | undefined
}

// CRITICAL: Ensure we're using PostgreSQL from DATABASE_URL
const databaseUrl = process.env.DATABASE_URL

if (!databaseUrl) {
  console.error('❌ CRITICAL: DATABASE_URL is not set!')
  throw new Error('DATABASE_URL environment variable is required')
}

// Verify it's PostgreSQL, not SQLite
if (databaseUrl.startsWith('file:') || databaseUrl.includes('dev.db')) {
  console.error('❌ CRITICAL: DATABASE_URL points to SQLite file! Expected PostgreSQL connection string.')
  throw new Error('DATABASE_URL must be a PostgreSQL connection string, not a file path')
}

// Log database connection info (without exposing password)
const dbInfo = databaseUrl.replace(/:[^:@]+@/, ':****@') // Hide password
console.log('🔌 Database connection:', dbInfo.includes('postgres') ? 'PostgreSQL ✅' : 'UNKNOWN ⚠️')

export const prisma =
  globalForPrisma.prisma ??
  new PrismaClient({
    log: process.env.NODE_ENV === 'development' ? ['error', 'warn'] : ['error'],
    datasources: {
      db: {
        url: databaseUrl,
      },
    },
  })

if (process.env.NODE_ENV !== 'production') {
  globalForPrisma.prisma = prisma
} else {
  // In production, ensure we don't create multiple instances
  if (!globalForPrisma.prisma) {
    globalForPrisma.prisma = prisma
  }
}



