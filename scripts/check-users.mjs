/**
 * Check existing users in the database
 * 
 * Usage: node scripts/check-users.mjs
 */

import { PrismaClient } from '@prisma/client'
import { readFileSync } from 'fs'
import { fileURLToPath } from 'url'
import { dirname, join } from 'path'

// Load .env.local file
const __filename = fileURLToPath(import.meta.url)
const __dirname = dirname(__filename)
const envPath = join(__dirname, '..', '.env.local')

try {
  const envFile = readFileSync(envPath, 'utf-8')
  envFile.split('\n').forEach(line => {
    const match = line.match(/^([^#=]+)=(.*)$/)
    if (match) {
      const key = match[1].trim()
      let value = match[2].trim()
      // Remove quotes if present
      if ((value.startsWith('"') && value.endsWith('"')) || 
          (value.startsWith("'") && value.endsWith("'"))) {
        value = value.slice(1, -1)
      }
      process.env[key] = value
    }
  })
} catch (error) {
  // .env.local might not exist, that's okay
}

const prisma = new PrismaClient()

async function checkUsers() {
  try {
    console.log('🔍 Checking users in database...\n')
    
    // Get all users
    const users = await prisma.user.findMany({
      select: {
        id: true,
        email: true,
        name: true,
        role: true,
        createdAt: true,
      },
      orderBy: {
        createdAt: 'desc'
      }
    })

    if (users.length === 0) {
      console.log('❌ No users found in the database.')
      console.log('\n💡 You can create an admin account by:')
      console.log('   1. Visit: http://localhost:3000/setup')
      console.log('   2. Or run: npm run force-admin')
      return
    }

    console.log(`✅ Found ${users.length} user(s):\n`)
    
    users.forEach((user, index) => {
      console.log(`${index + 1}. User Details:`)
      console.log(`   📧 Email: ${user.email}`)
      console.log(`   👤 Name: ${user.name}`)
      console.log(`   🔑 Role: ${user.role}`)
      console.log(`   📅 Created: ${user.createdAt}`)
      console.log('')
    })

    console.log('\n💡 You can login with any of these email addresses.')
    console.log('⚠️  Note: Passwords are hashed and cannot be retrieved.')
    console.log('   If you forgot your password, you can:')
    console.log('   1. Use the force-admin script to reset admin password')
    console.log('   2. Or create a new account via /setup (if no users exist)')

  } catch (error) {
    console.error('❌ Error checking users:')
    if (error.message.includes('DATABASE_URL')) {
      console.error('   Database connection error. Please check your DATABASE_URL in .env.local')
    } else {
      console.error(error.message)
    }
  } finally {
    await prisma.$disconnect()
  }
}

// Run the script
checkUsers()

