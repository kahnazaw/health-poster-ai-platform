'use client'

import { signOut, useSession } from 'next-auth/react'
import Link from 'next/link'
import { usePathname } from 'next/navigation'
import { motion } from 'framer-motion'
import { Activity, BarChart3, Users, FileText, LogOut as LogOutIcon, Menu, X } from 'lucide-react'
import { useState } from 'react'

export default function Navbar() {
  const { data: session } = useSession()
  const pathname = usePathname()
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false)

  if (!session) return null

  const role = (session.user as any).role
  const isAdmin = role === 'ADMIN' || role === 'SUPER_ADMIN'

  return (
    <motion.nav
      initial={{ y: -20, opacity: 0 }}
      animate={{ y: 0, opacity: 1 }}
      className="bg-white/95 backdrop-blur-sm shadow-lg border-b border-gray-200/50 sticky top-0 z-50"
    >
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          <div className="flex items-center space-x-8 space-x-reverse">
            <motion.div whileHover={{ scale: 1.05 }}>
              <Link href="/" className="flex items-center space-x-2 space-x-reverse">
                <div className="w-10 h-10 bg-gradient-to-br from-primary-500 to-sky-500 rounded-lg flex items-center justify-center">
                  <Activity className="w-6 h-6 text-white" />
                </div>
                <span className="text-xl font-bold bg-gradient-to-r from-primary-600 to-sky-600 bg-clip-text text-transparent">
                  منصة التوعية الصحية
                </span>
              </Link>
            </motion.div>
            
            {/* Desktop Menu */}
            <div className="hidden md:flex space-x-2 space-x-reverse">
              <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
                <Link
                  href="/posters/create"
                  className={`px-4 py-2 rounded-lg text-sm font-medium transition-all flex items-center space-x-2 space-x-reverse ${
                    pathname === '/posters/create'
                      ? 'bg-primary-100 text-primary-700 shadow-sm'
                      : 'text-gray-700 hover:bg-gray-50'
                  }`}
                >
                  <FileText className="w-4 h-4" />
                  <span>إنشاء بوستر</span>
                </Link>
              </motion.div>
              
              {isAdmin ? (
                <>
                  <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
                    <Link
                      href="/dashboard/admin"
                      className={`px-4 py-2 rounded-lg text-sm font-medium transition-all flex items-center space-x-2 space-x-reverse ${
                        pathname === '/dashboard/admin'
                          ? 'bg-primary-100 text-primary-700 shadow-sm'
                          : 'text-gray-700 hover:bg-gray-50'
                      }`}
                    >
                      <BarChart3 className="w-4 h-4" />
                      <span>لوحة التحكم</span>
                    </Link>
                  </motion.div>
                  <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
                    <Link
                      href="/dashboard/admin/analytics"
                      className={`px-4 py-2 rounded-lg text-sm font-medium transition-all flex items-center space-x-2 space-x-reverse ${
                        pathname === '/dashboard/admin/analytics'
                          ? 'bg-primary-100 text-primary-700 shadow-sm'
                          : 'text-gray-700 hover:bg-gray-50'
                      }`}
                    >
                      <BarChart3 className="w-4 h-4" />
                      <span>التحليلات</span>
                    </Link>
                  </motion.div>
                  <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
                    <Link
                      href="/dashboard/admin/logs"
                      className={`px-4 py-2 rounded-lg text-sm font-medium transition-all flex items-center space-x-2 space-x-reverse ${
                        pathname === '/dashboard/admin/logs'
                          ? 'bg-primary-100 text-primary-700 shadow-sm'
                          : 'text-gray-700 hover:bg-gray-50'
                      }`}
                    >
                      <Activity className="w-4 h-4" />
                      <span>سجل الأنشطة</span>
                    </Link>
                  </motion.div>
                  {(role === 'SUPER_ADMIN' || role === 'ADMIN') && (
                    <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
                      <Link
                        href="/dashboard/admin/organizations"
                        className={`px-4 py-2 rounded-lg text-sm font-medium transition-all flex items-center space-x-2 space-x-reverse ${
                          pathname === '/dashboard/admin/organizations'
                            ? 'bg-primary-100 text-primary-700 shadow-sm'
                            : 'text-gray-700 hover:bg-gray-50'
                        }`}
                      >
                        <Users className="w-4 h-4" />
                        <span>المنظمات</span>
                      </Link>
                    </motion.div>
                  )}
                </>
              ) : (
                <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
                  <Link
                    href="/dashboard/user"
                    className={`px-4 py-2 rounded-lg text-sm font-medium transition-all flex items-center space-x-2 space-x-reverse ${
                      pathname === '/dashboard/user'
                        ? 'bg-primary-100 text-primary-700 shadow-sm'
                        : 'text-gray-700 hover:bg-gray-50'
                    }`}
                  >
                    <BarChart3 className="w-4 h-4" />
                    <span>لوحة المستخدم</span>
                  </Link>
                </motion.div>
              )}
            </div>
          </div>
          
          <div className="flex items-center space-x-4 space-x-reverse">
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              className="hidden sm:flex items-center space-x-3 space-x-reverse px-4 py-2 bg-gradient-to-r from-primary-50 to-sky-50 rounded-lg"
            >
              <div className="w-8 h-8 bg-gradient-to-br from-primary-500 to-sky-500 rounded-full flex items-center justify-center text-white font-semibold text-sm">
                {(session.user?.name || 'U')[0]}
              </div>
              <span className="text-sm font-medium text-gray-700">{session.user?.name}</span>
            </motion.div>
            
            <motion.button
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={() => signOut({ callbackUrl: '/login' })}
              className="px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-100 rounded-lg transition-all flex items-center space-x-2 space-x-reverse"
            >
              <LogOutIcon className="w-4 h-4" />
              <span className="hidden sm:inline">تسجيل الخروج</span>
            </motion.button>
            
            {/* Mobile Menu Button */}
            <button
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              className="md:hidden p-2 rounded-lg hover:bg-gray-100"
            >
              {mobileMenuOpen ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
            </button>
          </div>
        </div>
        
        {/* Mobile Menu */}
        {mobileMenuOpen && (
          <motion.div
            initial={{ opacity: 0, height: 0 }}
            animate={{ opacity: 1, height: 'auto' }}
            exit={{ opacity: 0, height: 0 }}
            className="md:hidden py-4 border-t border-gray-200"
          >
            <div className="flex flex-col space-y-2">
              <Link href="/posters/create" className="px-4 py-2 rounded-lg hover:bg-gray-50">
                إنشاء بوستر
              </Link>
              {isAdmin ? (
                <>
                  <Link href="/dashboard/admin" className="px-4 py-2 rounded-lg hover:bg-gray-50">
                    لوحة التحكم
                  </Link>
                  <Link href="/dashboard/admin/analytics" className="px-4 py-2 rounded-lg hover:bg-gray-50">
                    التحليلات
                  </Link>
                  <Link href="/dashboard/admin/logs" className="px-4 py-2 rounded-lg hover:bg-gray-50">
                    سجل الأنشطة
                  </Link>
                </>
              ) : (
                <Link href="/dashboard/user" className="px-4 py-2 rounded-lg hover:bg-gray-50">
                  لوحة المستخدم
                </Link>
              )}
            </div>
          </motion.div>
        )}
      </div>
    </motion.nav>
  )
}
