FROM node:18-alpine AS base

# Install OpenSSL and required libraries for Prisma
RUN apk add --no-cache \
    openssl \
    libc6-compat \
    openssl-dev \
    ca-certificates

# Install dependencies only when needed
FROM base AS deps
WORKDIR /app

# Install dependencies based on the preferred package manager
COPY package.json package-lock.json* ./
RUN npm ci

# Rebuild the source code only when needed
FROM base AS builder
WORKDIR /app
COPY --from=deps /app/node_modules ./node_modules
COPY . .

# Generate Prisma Client with musl binary target
# CRITICAL: Skip environment validation during build - DATABASE_URL not required
ENV PRISMA_CLI_BINARY_TARGETS=linux-musl-openssl-3.0.x
ENV SKIP_ENV_VALIDATION=1
ENV DATABASE_URL=postgresql://placeholder:placeholder@localhost:5432/placeholder
RUN npx prisma generate

# Build Next.js
# CRITICAL: DATABASE_URL is NOT required during build - validation happens at runtime only
# The placeholder URL above is only for Prisma generate, actual connection happens at runtime
RUN SKIP_ENV_VALIDATION=1 npm run build

# Production image, copy all the files and run next
FROM base AS runner
WORKDIR /app

ENV NODE_ENV=production

# Install OpenSSL in runner stage (critical for Prisma runtime)
RUN apk add --no-cache \
    openssl \
    libc6-compat \
    ca-certificates

RUN addgroup --system --gid 1001 nodejs
RUN adduser --system --uid 1001 nextjs

COPY --from=builder /app/public ./public
COPY --from=builder --chown=nextjs:nodejs /app/.next/standalone ./
COPY --from=builder --chown=nextjs:nodejs /app/.next/static ./.next/static

# Copy Prisma files and engine for migrations
COPY --from=builder /app/prisma ./prisma
COPY --from=builder /app/node_modules/.prisma ./node_modules/.prisma
COPY --from=builder /app/node_modules/@prisma ./node_modules/@prisma

# Copy scripts folder for manual database operations
COPY --from=builder --chown=nextjs:nodejs /app/scripts ./scripts
COPY --from=builder /app/package.json ./package.json

# Prisma will automatically find binaries based on binaryTargets in schema.prisma
# No need to manually set PRISMA_* environment variables - Prisma handles this automatically

# Verify DATABASE_URL is set (Railway provides this automatically)
# The app will read DATABASE_URL from environment variables

USER nextjs

EXPOSE 3000

ENV PORT=3000
ENV HOSTNAME="0.0.0.0"

# Run migrations and start
# DATABASE_URL is available at runtime from Railway environment variables
CMD ["sh", "-c", "npx prisma migrate deploy && node server.js"]

