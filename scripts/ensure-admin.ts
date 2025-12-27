import { prisma } from '../lib/prisma'
import bcrypt from 'bcryptjs'

let adminCheckPromise: Promise<void> | null = null

export async function ensureAdmin() {
  // Ensure this only runs once, even if called multiple times
  if (adminCheckPromise) {
    return adminCheckPromise
  }

  adminCheckPromise = (async () => {
  try {
    const adminEmail = process.env.ADMIN_EMAIL
    const adminPassword = process.env.ADMIN_PASSWORD

    // Exit silently if credentials are missing
    if (!adminEmail || !adminPassword) {
      return
    }

    // Check if an admin user already exists
    const existingAdmin = await prisma.user.findFirst({
      where: { role: 'ADMIN' }
    })

    if (existingAdmin) {
      console.log('Admin already exists')
      return
    }

    // Check if a user with this email already exists
    const existingUser = await prisma.user.findUnique({
      where: { email: adminEmail }
    })

    if (existingUser) {
      // Update existing user to admin if needed
      if (existingUser.role !== 'ADMIN') {
        const hashedPassword = await bcrypt.hash(adminPassword, 10)
        await prisma.user.update({
          where: { email: adminEmail },
          data: {
            role: 'ADMIN',
            password: hashedPassword
          }
        })
        console.log('Admin user created')
      } else {
        console.log('Admin already exists')
      }
      return
    }

    // Create new admin user
    const hashedPassword = await bcrypt.hash(adminPassword, 10)
    await prisma.user.create({
      data: {
        email: adminEmail,
        password: hashedPassword,
        name: 'Administrator',
        role: 'ADMIN'
      }
    })

    console.log('Admin user created')
  } catch (error) {
    // Silently handle errors to prevent startup crashes
    console.error('Error ensuring admin user:', error instanceof Error ? error.message : 'Unknown error')
  } finally {
    // Reset promise after completion to allow retry if needed
    adminCheckPromise = null
  }
  })()

  return adminCheckPromise
}

