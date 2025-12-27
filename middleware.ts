import { withAuth } from 'next-auth/middleware'
import { NextResponse } from 'next/server'

export default withAuth(
  function middleware(req) {
    const token = req.nextauth.token
    const path = req.nextUrl.pathname
    const role = (token as any)?.role

    // Allow access to login page without auth
    if (path === '/login') {
      if (token) {
        // Redirect authenticated users to their dashboard
        if (role === 'ADMIN') {
          return NextResponse.redirect(new URL('/dashboard/admin', req.url))
        }
        return NextResponse.redirect(new URL('/dashboard/user', req.url))
      }
      return NextResponse.next()
    }

    // Protect dashboard routes
    if (path.startsWith('/dashboard')) {
      if (!token) {
        return NextResponse.redirect(new URL('/login', req.url))
      }

      // Admin routes - ADMIN only
      if (path.startsWith('/dashboard/admin')) {
        if (role !== 'ADMIN') {
          return NextResponse.redirect(new URL('/dashboard/user', req.url))
        }
      }

      // User routes - authenticated USER or ADMIN
      if (path.startsWith('/dashboard/user')) {
        // Both USER and ADMIN can access
        if (!role || (role !== 'USER' && role !== 'ADMIN')) {
          return NextResponse.redirect(new URL('/login', req.url))
        }
      }
    }

    // Protect poster routes - authenticated users only
    if (path.startsWith('/posters')) {
      if (!token) {
        return NextResponse.redirect(new URL('/login', req.url))
      }
    }

    return NextResponse.next()
  },
  {
    callbacks: {
      authorized: ({ token, req }) => {
        const path = req.nextUrl.pathname
        if (path === '/login') return true
        return !!token
      },
    },
  }
)

export const config = {
  matcher: ['/dashboard/:path*', '/login', '/posters/:path*'],
}

