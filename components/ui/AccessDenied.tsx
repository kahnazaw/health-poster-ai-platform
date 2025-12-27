'use client'

import { motion } from 'framer-motion'
import { ShieldAlert, ArrowLeft } from 'lucide-react'
import Link from 'next/link'
import Button from './Button'

export default function AccessDenied() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-red-50 via-orange-50 to-yellow-50 px-4">
      <motion.div
        initial={{ opacity: 0, scale: 0.9, y: 20 }}
        animate={{ opacity: 1, scale: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="text-center max-w-md w-full"
      >
        <motion.div
          animate={{
            rotate: [0, -10, 10, -10, 0],
          }}
          transition={{
            duration: 0.5,
            repeat: Infinity,
            repeatDelay: 2,
          }}
          className="mb-6 flex justify-center"
        >
          <div className="p-4 bg-red-100 rounded-full">
            <ShieldAlert className="w-16 h-16 text-red-600" />
          </div>
        </motion.div>

        <motion.h1
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
          className="text-4xl font-bold text-gray-900 mb-4"
        >
          الوصول مرفوض
        </motion.h1>

        <motion.p
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3 }}
          className="text-gray-600 mb-8 text-lg"
        >
          ليس لديك الصلاحيات اللازمة للوصول إلى هذه الصفحة.
          <br />
          يرجى التواصل مع المدير للحصول على الصلاحيات المناسبة.
        </motion.p>

        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.4 }}
        >
          <Link href="/dashboard/user">
            <Button icon={ArrowLeft} iconPosition="right">
              العودة إلى لوحة التحكم
            </Button>
          </Link>
        </motion.div>
      </motion.div>
    </div>
  )
}

