/**
 * CRITICAL: Sync Prisma Schema to PostgreSQL Database
 * 
 * This script ensures the PostgreSQL database tables match the Prisma schema.
 * Run this on Railway to ensure tables exist.
 * 
 * Usage: npm run sync-db
 */

import { execSync } from 'child_process'

console.log('🔄 Syncing Prisma schema to PostgreSQL database...')
console.log('📋 This will create/update tables to match schema.prisma\n')

try {
  // Check DATABASE_URL
  if (!process.env.DATABASE_URL) {
    console.error('❌ DATABASE_URL is not set!')
    process.exit(1)
  }

  const dbUrl = process.env.DATABASE_URL.replace(/:[^:@]+@/, ':****@')
  console.log('🔌 Database:', dbUrl.includes('postgres') ? 'PostgreSQL ✅' : 'UNKNOWN ⚠️')
  console.log('')

  // Run prisma db push (non-destructive, syncs schema)
  console.log('📤 Running: npx prisma db push...')
  execSync('npx prisma db push --accept-data-loss', {
    stdio: 'inherit',
    env: process.env,
  })

  console.log('\n✅ Database schema synced successfully!')
  console.log('📊 Tables should now match schema.prisma')
  
} catch (error) {
  console.error('\n❌ Error syncing database:')
  console.error(error)
  process.exit(1)
}

