import { NextAuthOptions } from 'next-auth'
import { PrismaAdapter } from '@next-auth/prisma-adapter'
import CredentialsProvider from 'next-auth/providers/credentials'
import { prisma } from './prisma'
import bcrypt from 'bcryptjs'
import { logUserLogin } from './logger'

if (!process.env.NEXTAUTH_SECRET && !process.env.AUTH_SECRET) {
  console.error('❌ NEXTAUTH_SECRET is missing! Please set it in Railway environment variables.')
}

if (!process.env.NEXTAUTH_URL) {
  console.warn('⚠️ NEXTAUTH_URL is missing! NextAuth may not work correctly.')
}

export const authOptions: NextAuthOptions = {
  adapter: PrismaAdapter(prisma),
  providers: [
    CredentialsProvider({
      name: 'Credentials',
      credentials: {
        email: { label: 'البريد الإلكتروني', type: 'email' },
        password: { label: 'كلمة المرور', type: 'password' }
      },
      async authorize(credentials) {
        if (!credentials?.email || !credentials?.password) {
          return null
        }

        try {
          // Test database connection
          await prisma.$connect()
          
          const user = await prisma.user.findUnique({
            where: { email: credentials.email }
          })

          if (!user) {
            console.log(`⚠️ Login attempt: User not found - ${credentials.email}`)
            return null
          }

          const isPasswordValid = await bcrypt.compare(
            credentials.password,
            user.password
          )

          if (!isPasswordValid) {
            console.log(`⚠️ Login attempt: Invalid password for ${credentials.email}`)
            return null
          }

          // Log successful login
          try {
            await logUserLogin(user.name, user.role, user.email)
          } catch (error) {
            // Don't fail login if logging fails
            console.error('Failed to log user login:', error)
          }

          return {
            id: user.id,
            email: user.email,
            name: user.name,
            role: user.role as 'ADMIN' | 'USER',
          }
        } catch (error: any) {
          console.error('❌ Database error during login:', error.message)
          // Return null on database errors (don't expose error details)
          return null
        }
      }
    })
  ],
  callbacks: {
    async jwt({ token, user }) {
      if (user) {
        token.role = (user as any).role
        token.id = (user as any).id
      }
      return token
    },
    async session({ session, token }) {
      if (session.user) {
        (session.user as any).role = token.role as 'ADMIN' | 'USER'
        (session.user as any).id = token.id
      }
      return session
    },
    async signIn({ user }) {
      // Allow sign in
      return true
    },
    async redirect({ url, baseUrl }) {
      // Handle redirect after login
      if (url.startsWith('/')) {
        return `${baseUrl}${url}`
      }
      if (new URL(url).origin === baseUrl) {
        return url
      }
      return baseUrl
    }
  },
  pages: {
    signIn: '/login',
  },
  session: {
    strategy: 'jwt',
  },
  secret: process.env.NEXTAUTH_SECRET || process.env.AUTH_SECRET,
  debug: process.env.NODE_ENV === 'development',
}



