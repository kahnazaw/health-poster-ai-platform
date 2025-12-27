import { withAuth } from 'next-auth/middleware'
import { NextResponse } from 'next/server'

export default withAuth(
  function middleware(req) {
    const token = req.nextauth.token
    const path = req.nextUrl.pathname

    // Allow access to login page without auth
    if (path === '/login') {
      if (token) {
        return NextResponse.redirect(new URL('/', req.url))
      }
      return NextResponse.next()
    }

    // Protect dashboard routes
    if (path.startsWith('/dashboard')) {
      if (!token) {
        return NextResponse.redirect(new URL('/login', req.url))
      }

      // Admin routes
      if (path.startsWith('/dashboard/admin')) {
        if ((token as any).role !== 'ADMIN') {
          return NextResponse.redirect(new URL('/dashboard/user', req.url))
        }
      }
    }

    // Protect poster routes
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

