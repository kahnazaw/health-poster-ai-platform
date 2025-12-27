import { PrismaClient } from '@prisma/client'
import bcrypt from 'bcryptjs'

const prisma = new PrismaClient()

async function main() {
  // إنشاء مستخدم مدير افتراضي
  const adminEmail = 'admin@health.gov.iq'
  const adminPassword = 'Admin@123'

  const existingAdmin = await prisma.user.findUnique({
    where: { email: adminEmail },
  })

  if (!existingAdmin) {
    const hashedPassword = await bcrypt.hash(adminPassword, 10)
    
    await prisma.user.create({
      data: {
        email: adminEmail,
        password: hashedPassword,
        name: 'مدير النظام',
        role: 'ADMIN',
      },
    })

    console.log('✅ تم إنشاء حساب المدير الافتراضي')
    console.log(`📧 البريد: ${adminEmail}`)
    console.log(`🔑 كلمة المرور: ${adminPassword}`)
  } else {
    console.log('ℹ️  حساب المدير موجود بالفعل')
  }
}

main()
  .catch((e) => {
    console.error(e)
    process.exit(1)
  })
  .finally(async () => {
    await prisma.$disconnect()
  })




