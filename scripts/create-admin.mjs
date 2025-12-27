/**
 * EMERGENCY: Force Create Admin Account
 * 
 * This script directly creates/updates an admin user in the database
 * bypassing all API and UI restrictions.
 * 
 * Usage: npm run force-admin
 */

import { PrismaClient } from '@prisma/client'
import bcrypt from 'bcryptjs'

const prisma = new PrismaClient()

const ADMIN_EMAIL = 'admin@kirkuk.health'
const ADMIN_PASSWORD = 'Password123!'
const ADMIN_NAME = 'مدير النظام'
const ADMIN_ROLE = 'ADMIN'

async function createAdmin() {
  try {
    console.log('🔧 Starting admin account creation...')
    console.log(`📧 Email: ${ADMIN_EMAIL}`)
    console.log(`👤 Name: ${ADMIN_NAME}`)
    console.log(`🔑 Role: ${ADMIN_ROLE}`)

    // Hash the password
    console.log('🔐 Hashing password...')
    const hashedPassword = await bcrypt.hash(ADMIN_PASSWORD, 12)
    console.log('✅ Password hashed successfully')

    // Check if user already exists
    const existingUser = await prisma.user.findUnique({
      where: { email: ADMIN_EMAIL },
    })

    if (existingUser) {
      console.log('⚠️  User already exists. Updating to ADMIN...')
      
      // Update existing user
      const updatedUser = await prisma.user.update({
        where: { email: ADMIN_EMAIL },
        data: {
          password: hashedPassword,
          name: ADMIN_NAME,
          role: ADMIN_ROLE,
        },
        select: {
          id: true,
          email: true,
          name: true,
          role: true,
          createdAt: true,
        },
      })

      console.log('✅ Admin account updated successfully!')
      console.log('📋 User Details:')
      console.log(`   ID: ${updatedUser.id}`)
      console.log(`   Email: ${updatedUser.email}`)
      console.log(`   Name: ${updatedUser.name}`)
      console.log(`   Role: ${updatedUser.role}`)
      console.log(`   Created: ${updatedUser.createdAt}`)
    } else {
      console.log('➕ Creating new admin user...')
      
      // Create new user
      const newUser = await prisma.user.create({
        data: {
          email: ADMIN_EMAIL,
          password: hashedPassword,
          name: ADMIN_NAME,
          role: ADMIN_ROLE,
        },
        select: {
          id: true,
          email: true,
          name: true,
          role: true,
          createdAt: true,
        },
      })

      console.log('✅ Admin account created successfully!')
      console.log('📋 User Details:')
      console.log(`   ID: ${newUser.id}`)
      console.log(`   Email: ${newUser.email}`)
      console.log(`   Name: ${newUser.name}`)
      console.log(`   Role: ${newUser.role}`)
      console.log(`   Created: ${newUser.createdAt}`)
    }

    console.log('\n🎉 SUCCESS! You can now login with:')
    console.log(`   Email: ${ADMIN_EMAIL}`)
    console.log(`   Password: ${ADMIN_PASSWORD}`)
    console.log('\n⚠️  IMPORTANT: Change this password after first login!')

  } catch (error) {
    console.error('❌ Error creating admin account:')
    console.error(error)
    process.exit(1)
  } finally {
    await prisma.$disconnect()
  }
}

// Run the script
createAdmin()

