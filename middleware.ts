import { withAuth } from 'next-auth/middleware'
import { NextResponse } from 'next/server'
import type { NextRequest } from 'next/server'

// Simple middleware wrapper to check setup status
async function checkSetupAndRedirect(req: NextRequest) {
  if (req.nextUrl.pathname === '/setup') {
    try {
      // Check if users exist by calling our API
      const baseUrl = req.nextUrl.origin
      const statusRes = await fetch(`${baseUrl}/api/setup/status`, {
        cache: 'no-store',
      })
      
      if (statusRes.ok) {
        const data = await statusRes.json()
        if (!data.canSetup) {
          // Users exist, redirect to login
          return NextResponse.redirect(new URL('/login', req.url))
        }
      }
    } catch (error) {
      // On error, allow access (safer for first-time setup)
      console.error('Error checking setup status:', error)
    }
  }
  return null
}

export default withAuth(
  function middleware(req) {
    const token = req.nextauth.token
    const path = req.nextUrl.pathname
    const role = (token as any)?.role

    // Handle setup page
    // EMERGENCY: Temporarily allow access to /setup without restrictions
    if (path === '/setup') {
      // Allow access to setup page - bypassing all checks for emergency reset
      return NextResponse.next()
    }

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
        if (role !== 'ADMIN' && role !== 'SUPER_ADMIN') {
          return NextResponse.redirect(new URL('/dashboard/user', req.url))
        }
      }

      // User routes - authenticated USER or ADMIN
      if (path.startsWith('/dashboard/user')) {
        // Both USER and ADMIN can access
        if (!role || (role !== 'USER' && role !== 'ADMIN' && role !== 'SUPER_ADMIN')) {
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
        // Allow login and setup pages without auth
        if (path === '/login' || path === '/setup') return true
        return !!token
      },
    },
  }
)

export const config = {
  matcher: ['/dashboard/:path*', '/login', '/setup', '/posters/:path*'],
  // Explicitly exclude API routes from middleware
  // API routes should not be blocked by middleware
}
