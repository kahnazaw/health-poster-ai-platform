/**
 * Prisma Seed Script
 * 
 * Creates a default admin account and organization if they don't exist.
 * This script is idempotent and safe to run multiple times.
 * 
 * Usage:
 *   - Manual: railway run npm run seed
 *   - Via Prisma: npx prisma db seed
 */

import { PrismaClient } from '@prisma/client'
import bcrypt from 'bcryptjs'

const prisma = new PrismaClient()

// Admin account data
const ADMIN_EMAIL = 'admin@kirkuk.health'
const ADMIN_PASSWORD = '@#Eng1990'
const ADMIN_NAME = 'alaa saleh ahmed'
const ADMIN_ROLE = 'ADMIN' // Using uppercase to match schema conventions

// Organization data
const ORG_NAME = 'دائرة صحة كركوك – قطاع كركوك الأول – وحدة تعزيز الصحة'
const ORG_CODE = 'KIRKUK-H1'

async function main() {
  console.log('🌱 Starting seed script...\n')

  try {
    // Check if admin user already exists
    const existingUser = await prisma.user.findUnique({
      where: { email: ADMIN_EMAIL },
      include: { organization: true },
    })

    if (existingUser) {
      console.log('✅ Admin user already exists:')
      console.log(`   📧 Email: ${existingUser.email}`)
      console.log(`   👤 Name: ${existingUser.name}`)
      console.log(`   🔑 Role: ${existingUser.role}`)
      if (existingUser.organization) {
        console.log(`   🏢 Organization: ${existingUser.organization.name}`)
      }
      console.log('\n✨ Seed script completed - no changes needed.')
      return
    }

    console.log('📝 Admin user not found. Creating admin account and organization...\n')

    // Find or create organization
    let organization = await prisma.organization.findUnique({
      where: { code: ORG_CODE },
    })

    if (!organization) {
      console.log(`🏢 Creating organization: ${ORG_NAME}`)
      organization = await prisma.organization.create({
        data: {
          name: ORG_NAME,
          code: ORG_CODE,
          description: 'المنظمة الرئيسية لمنصة بوسترات التوعية الصحية',
        },
      })
      console.log(`✅ Organization created with ID: ${organization.id}\n`)
    } else {
      console.log(`✅ Organization already exists: ${organization.name}\n`)
    }

    // Hash password with 12 rounds
    console.log('🔐 Hashing password...')
    const hashedPassword = await bcrypt.hash(ADMIN_PASSWORD, 12)
    console.log('✅ Password hashed successfully\n')

    // Create admin user
    console.log('👤 Creating admin user...')
    const adminUser = await prisma.user.create({
      data: {
        email: ADMIN_EMAIL,
        password: hashedPassword,
        name: ADMIN_NAME,
        role: ADMIN_ROLE,
        organizationId: organization.id,
      },
    })

    console.log('✅ Admin user created successfully!')
    console.log('\n📋 Account Details:')
    console.log(`   📧 Email: ${adminUser.email}`)
    console.log(`   👤 Name: ${adminUser.name}`)
    console.log(`   🔑 Role: ${adminUser.role}`)
    console.log(`   🏢 Organization: ${organization.name}`)
    console.log(`   🆔 User ID: ${adminUser.id}`)
    console.log('\n🎉 Seed script completed successfully!')
    console.log('\n⚠️  IMPORTANT: Change the default password after first login!')

  } catch (error) {
    console.error('❌ Error during seed:')
    if (error instanceof Error) {
      console.error(`   ${error.message}`)
      if (error.stack) {
        console.error(`\nStack trace:\n${error.stack}`)
      }
    } else {
      console.error('   Unknown error:', error)
    }
    process.exit(1)
  } finally {
    await prisma.$disconnect()
  }
}

// Execute the seed function
main()
  .catch((error) => {
    console.error('❌ Unhandled error in seed script:')
    console.error(error)
    process.exit(1)
  })

