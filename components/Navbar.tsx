'use client'

import { signOut, useSession } from 'next-auth/react'
import Link from 'next/link'
import { usePathname } from 'next/navigation'

export default function Navbar() {
  const { data: session } = useSession()
  const pathname = usePathname()

  if (!session) return null

  const role = (session.user as any).role
  const isAdmin = role === 'ADMIN'

  return (
    <nav className="bg-white shadow-md border-b border-gray-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          <div className="flex items-center space-x-8 space-x-reverse">
            <Link href="/" className="text-xl font-bold text-primary-700">
              منصة التوعية الصحية
            </Link>
            <div className="flex space-x-6 space-x-reverse">
              <Link
                href="/posters/create"
                className={`px-3 py-2 rounded-md text-sm font-medium transition ${
                  pathname === '/posters/create'
                    ? 'bg-primary-100 text-primary-700'
                    : 'text-gray-700 hover:bg-gray-100'
                }`}
              >
                إنشاء بوستر
              </Link>
              {isAdmin ? (
                <>
                  <Link
                    href="/dashboard/admin"
                    className={`px-3 py-2 rounded-md text-sm font-medium transition ${
                      pathname === '/dashboard/admin'
                        ? 'bg-primary-100 text-primary-700'
                        : 'text-gray-700 hover:bg-gray-100'
                    }`}
                  >
                    لوحة التحكم
                  </Link>
                  <Link
                    href="/dashboard/admin/analytics"
                    className={`px-3 py-2 rounded-md text-sm font-medium transition ${
                      pathname === '/dashboard/admin/analytics'
                        ? 'bg-primary-100 text-primary-700'
                        : 'text-gray-700 hover:bg-gray-100'
                    }`}
                  >
                    التحليلات
                  </Link>
                  {(role === 'SUPER_ADMIN' || role === 'ADMIN') && (
                    <Link
                      href="/dashboard/admin/organizations"
                      className={`px-3 py-2 rounded-md text-sm font-medium transition ${
                        pathname === '/dashboard/admin/organizations'
                          ? 'bg-primary-100 text-primary-700'
                          : 'text-gray-700 hover:bg-gray-100'
                      }`}
                    >
                      المنظمات
                    </Link>
                  )}
                </>
              ) : (
                <Link
                  href="/dashboard/user"
                  className={`px-3 py-2 rounded-md text-sm font-medium transition ${
                    pathname === '/dashboard/user'
                      ? 'bg-primary-100 text-primary-700'
                      : 'text-gray-700 hover:bg-gray-100'
                  }`}
                >
                  لوحة المستخدم
                </Link>
              )}
            </div>
          </div>
          <div className="flex items-center space-x-4 space-x-reverse">
            <span className="text-sm text-gray-600">
              {session.user?.name}
            </span>
            <button
              onClick={() => signOut({ callbackUrl: '/login' })}
              className="px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-100 rounded-md transition"
            >
              تسجيل الخروج
            </button>
          </div>
        </div>
      </div>
    </nav>
  )
}




